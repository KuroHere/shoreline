package net.shoreline.client.impl.module.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.NumberDisplay;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.network.AttackBlockEvent;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.math.position.PositionUtil;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.*;

/**
 * @author xgraza, Shoreline
 * @since 1.0
 */
public final class AutoMineModule extends ToggleModule
{
    Config<Boolean> multitaskConfig = new BooleanConfig("Multitask", "Allows mining while using items", true);
    Config<Float> breakRangeConfig = new NumberConfig<>("Range", "How far to break blocks from", 1.0f, 4.5f, 6.0f);
    Config<Float> damageConfig = new NumberConfig<>("Damage", "The minimum amount of damage done to a block before attempting to break", 0.0f, 0.7f, 1.0f, NumberDisplay.PERCENT);
    Config<Boolean> instantRemineConfig = new BooleanConfig("Instant", "Instantly remines a block that was previously mined", true);
    // Config<Boolean> doubleMineConfig = new BooleanConfig("DoubleMine", "If to mine two blocks at once", false);
    Config<Boolean> autoCityConfig = new BooleanConfig("Auto", "Automatically mines city blocks (including burrow)", false);
    Config<Boolean> autoRemineConfig = new BooleanConfig("AutoRemine", "Automatically remines blocks that were previously mined", false, () -> autoCityConfig.getValue());
    Config<Boolean> grimConfig = new BooleanConfig("Grim", "trade secrets", false);

    private BlockBreakData data;
    private long lastBreakTime;
    private boolean manualOverride;

    public AutoMineModule()
    {
        super("AutoMine", "Automatically mines blocks or surrounds", ModuleCategory.WORLD);
    }

    @Override
    public String getModuleData()
    {
        if (data != null)
        {
            return String.format("%.1f", Math.min(data.getBlockDamage(), 1.0f));
        }
        return super.getModuleData();
    }

    @Override
    protected void onDisable()
    {
        if (data != null && data.getBlockDamage() > 0.0f)
        {
            abortMining(data);
        }
        data = null;
        manualOverride = false;
        Managers.INVENTORY.syncToClient();
    }

    @EventListener
    public void onPlayerTick(final PlayerTickEvent event)
    {
        if (autoCityConfig.getValue() && !manualOverride && (data == null || mc.world.isAir(data.getPos())))
        {
            final BlockPos cityBlockPos = getAutoMineTarget();
            if (cityBlockPos != null  && !isThrottling())
            {
                // If we are re-mining, bypass throttle check below
                if (data instanceof AutoBlockBreakData && data.isRemine() && !mc.world.isAir(data.getPos()) && autoRemineConfig.getValue())
                {
                    attemptMine(data);
                }
                else if (!mc.world.isAir(cityBlockPos))
                {
                    if (data != null && data.getBlockDamage() > 0.0f)
                    {
                        abortMining(data);
                    }
                    data = new AutoBlockBreakData(cityBlockPos,
                            Direction.UP,
                            Modules.AUTO_TOOL.getBestToolNoFallback(mc.world.getBlockState(cityBlockPos)));
                    startMining(data);
                }
            }
        }
        if (data != null)
        {
            final double distance = data.getPos().getSquaredDistance(
                    mc.player.getX(), mc.player.getY(), mc.player.getZ());
            if (distance > ((NumberConfig<Float>) breakRangeConfig).getValueSq())
            {
                abortMining(data);
                data = null;
                return;
            }
            if (data.getState().isReplaceable())
            {
                // Once we broke the block that overrode that the auto city, we can allow the module
                // to auto mine "city" blocks
                if (manualOverride)
                {
                    manualOverride = false;
                    data = null;
                    return;
                }
                if (instantRemineConfig.getValue())
                {
                    if (data instanceof AutoBlockBreakData && !autoRemineConfig.getValue()) {
                        data = null;
                        return;
                    }
                    data.setRemine(true);
                    data.setDamage(1.0f);
                }
                else
                {
                    data.resetDamage();
                }
                return;
            }
            final float damageDelta = Modules.SPEEDMINE.calcBlockBreakingDelta(
                    data.getState(), mc.world, data.getPos());
            if (data.damage(damageDelta) >= damageConfig.getValue() || data.isRemine())
            {
                if (mc.world.isAir(data.getPos()))
                {
                    return;
                }
                if (mc.player.isUsingItem() && !multitaskConfig.getValue())
                {
                    return;
                }
                attemptMine(data);
            }
        }
    }

    @EventListener
    public void onAttackBlock(final AttackBlockEvent event)
    {
        // Do not try to break unbreakable blocks
        if (event.getState().getBlock().getHardness() == -1.0f || mc.player.isCreative())
        {
            return;
        }
        event.cancel();
        if (data != null)
        {
            if (data.getPos().equals(event.getPos()))
            {
                return;
            }
            abortMining(data);
        }
        else if (autoCityConfig.getValue())
        {
            // Only count as an override if AutoCity is doing something
            if (data instanceof AutoBlockBreakData)
            {
                abortMining(data);
                manualOverride = true;
            }
        }
        data = new BlockBreakData(event.getPos(),
                event.getDirection(),
                Modules.AUTO_TOOL.getBestToolNoFallback(event.getState()));
        startMining(data);
    }

    @EventListener
    public void onRenderWorld(final RenderWorldEvent event)
    {
        if (data != null && !mc.player.isCreative())
        {
            BlockPos mining = data.getPos();
            VoxelShape outlineShape = data.getState().getOutlineShape(mc.world, mining);
            outlineShape = outlineShape.isEmpty() ? VoxelShapes.fullCube() : outlineShape;
            Box render1 = outlineShape.getBoundingBox();
            Box render = new Box(mining.getX() + render1.minX, mining.getY() + render1.minY,
                    mining.getZ() + render1.minZ, mining.getX() + render1.maxX,
                    mining.getY() + render1.maxY, mining.getZ() + render1.maxZ);
            Vec3d center = render.getCenter();
            float scale = MathHelper.clamp(data.getBlockDamage() / damageConfig.getValue(), 0, 1.0f);
            double dx = (render1.maxX - render1.minX) / 2.0;
            double dy = (render1.maxY - render1.minY) / 2.0;
            double dz = (render1.maxZ - render1.minZ) / 2.0;
            final Box scaled = new Box(center, center).expand(dx * scale, dy * scale, dz * scale);
            RenderManager.renderBox(event.getMatrices(), scaled,
                    data.getBlockDamage() > (0.95f * damageConfig.getValue()) ? 0x6000ff00 : 0x60ff0000);
            RenderManager.renderBoundingBox(event.getMatrices(), scaled,
                    2.5f, data.getBlockDamage() > (0.95f * damageConfig.getValue()) ? 0x6000ff00 : 0x60ff0000);
        }
    }

    private void startMining(final BlockBreakData blockBreakData)
    {
        // Grim does not check for slot changes, and it bases its original predicted block
        // break calculation on the ItemStack you have when you first send the START_DESTROY_BLOCK
        // https://github.com/GrimAnticheat/Grim/blob/2.0/src/main/java/ac/grim/grimac/checks/impl/misc/FastBreak.java#L76
        // https://github.com/GrimAnticheat/Grim/blob/2.0/src/main/java/ac/grim/grimac/checks/impl/misc/FastBreak.java#L98
        // To prevent a FastBreak flag, we should just swap quickly so grim can calculate and fuck off
        // This should also work on other AntiCheats that do the same thing
        if (blockBreakData.getSlot() != -1 && grimConfig.getValue())
        {
            Managers.INVENTORY.setSlot(blockBreakData.getSlot());
        }
        Managers.NETWORK.sendSequencedPacket((sequence) -> new PlayerActionC2SPacket(
                START_DESTROY_BLOCK, blockBreakData.getPos(), blockBreakData.getDirection(), sequence));
        if (blockBreakData.getSlot() != -1 && grimConfig.getValue())
        {
            Managers.INVENTORY.syncToClient();
        }
    }

    private void attemptMine(final BlockBreakData blockBreakData)
    {
        if (blockBreakData.getSlot() != -1)
        {
            Managers.INVENTORY.setSlot(blockBreakData.getSlot());
        }
        Managers.NETWORK.sendSequencedPacket((sequence) -> new PlayerActionC2SPacket(
                STOP_DESTROY_BLOCK, blockBreakData.getPos(), blockBreakData.getDirection(), sequence));
        if (grimConfig.getValue())
        {
            Managers.NETWORK.sendSequencedPacket((sequence) -> new PlayerActionC2SPacket(
                    ABORT_DESTROY_BLOCK, blockBreakData.getPos().up(500),
                    blockBreakData.getDirection(), sequence));
        }
        if (blockBreakData.getSlot() != -1)
        {
            Managers.INVENTORY.syncToClient();
        }
    }

    private void abortMining(final BlockBreakData blockBreakData)
    {
        if (blockBreakData.getBlockDamage() > 0.0f)
        {
            Managers.NETWORK.sendSequencedPacket((sequence) -> new PlayerActionC2SPacket(
                    ABORT_DESTROY_BLOCK, blockBreakData.getPos(), blockBreakData.getDirection(), sequence));
            if (blockBreakData.getSlot() != -1)
            {
                Managers.INVENTORY.syncToClient();
            }
        }
    }

    private boolean isThrottling()
    {
        // Grim has an autistic check for breaking blocks in quick succession
        // https://github.com/GrimAnticheat/Grim/blob/2.0/src/main/java/ac/grim/grimac/checks/impl/misc/FastBreak.java#L80
        return System.currentTimeMillis() - lastBreakTime <= 280 && grimConfig.getValue();
    }

    private Set<BlockPos> getPotentialBlockTargets(PlayerEntity player)
    {
        final Set<BlockPos> potentialTargets = new HashSet<>();
        for (final BlockPos pos : PositionUtil.getAllInBox(player.getBoundingBox(), player.getBlockPos()))
        {
            if (mc.player.getEyePos().distanceTo(pos.toCenterPos()) > breakRangeConfig.getValue())
            {
                continue;
            }
            for (final Direction direction : Direction.values())
            {
                if (!direction.getAxis().isHorizontal())
                {
                    continue;
                }
                final BlockPos feetPos = pos.offset(direction);
                if (mc.player.getEyePos().distanceTo(feetPos.toCenterPos()) > breakRangeConfig.getValue())
                {
                    continue;
                }
                if (!autoRemineConfig.getValue() && mc.world.isAir(feetPos))
                {
                    continue;
                }
                potentialTargets.add(feetPos);
            }
        }
        return potentialTargets;
    }

    private BlockPos getAutoMineTarget()
    {
        BlockPos targetBlockPos = null;
        double minDist = Float.MAX_VALUE;
        for (final PlayerEntity player : mc.world.getPlayers())
        {
            if (player.equals(mc.player) || Managers.SOCIAL.isFriend(player.getGameProfile().getName()))
            {
                continue;
            }
            if (!mc.world.isAir(player.getBlockPos())) {
                return player.getBlockPos();
            }
            final Set<BlockPos> mineTargets = getPotentialBlockTargets(player);
            for (final BlockPos pos : mineTargets)
            {
                final double dist = mc.player.getEyePos().distanceTo(pos.toCenterPos());
                if (dist < minDist)
                {
                    minDist = dist;
                    targetBlockPos = pos;
                }
            }
        }
        return targetBlockPos;
    }

    private static class AutoBlockBreakData extends BlockBreakData
    {
        public AutoBlockBreakData(BlockPos pos, Direction direction, int slot)
        {
            super(pos, direction, slot);
        }
    }

    private static class BlockBreakData
    {
        private final BlockPos pos;
        private final Direction direction;
        private final int slot;

        private float blockDamage;
        private boolean remine;

        public BlockBreakData(final BlockPos pos, final Direction direction, final int slot)
        {
            this.pos = pos;
            this.direction = direction;
            this.slot = slot;
        }

        public float damage(final float dmg)
        {
            blockDamage += dmg;
            return blockDamage;
        }

        public void setDamage(float dmg) {
            blockDamage = dmg;
        }

        public void resetDamage()
        {
            remine = false;
            blockDamage = 0.0f;
        }

        public float getBlockDamage()
        {
            return blockDamage;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public Direction getDirection()
        {
            return direction;
        }

        public int getSlot()
        {
            return slot;
        }

        public BlockState getState()
        {
            return mc.world.getBlockState(pos);
        }

        public void setRemine(boolean remine)
        {
            this.remine = remine;
        }

        public boolean isRemine()
        {
            return remine;
        }
    }
}
