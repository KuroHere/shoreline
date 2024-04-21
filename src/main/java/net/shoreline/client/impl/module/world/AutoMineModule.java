package net.shoreline.client.impl.module.world;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.RotationModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.network.AttackBlockEvent;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.player.RotationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shoreline
 * @since 1.0
 */
public class AutoMineModule extends RotationModule {

    Config<Boolean> autoConfig = new BooleanConfig("Auto", "Automatically mines nearby players feet", false);
    Config<Float> enemyRangeConfig = new NumberConfig<>("EnemyRange", "Range to search for targets", 1.0f, 5.0f, 10.0f, () -> autoConfig.getValue());
    // Config<Boolean> doubleBreakConfig = new BooleanConfig("DoubleBreak", "Allows you to mine two blocks at once", false);
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range to mine blocks", 0.1f, 4.0f, 5.0f);
    Config<Float> speedConfig = new NumberConfig<>("Speed", "The speed to mine blocks", 0.1f, 1.0f, 1.0f);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotates when mining the block", true);
    Config<Swap> swapConfig = new EnumConfig<>("AutoSwap", "Swaps to the best tool once the mining is complete", Swap.SILENT, Swap.values());
    Config<Boolean> strictConfig = new BooleanConfig("Strict", "Swaps to tool using alternative packets to bypass NCP silent swap", false, () -> swapConfig.getValue() != Swap.OFF);
    Config<Boolean> grimConfig = new BooleanConfig("Grim", "Uses grim block breaking speeds", false);
    Config<Boolean> remineConfig = new BooleanConfig("Remine", "Automatically remines mined blocks", true);
    Config<Boolean> debugConfig = new BooleanConfig("Debug", "If to show debug information in the module metadata", false);
    //
    private MiningData miningData;
    private FailMiningData failMiningData;
    private boolean tryBreak;

    public AutoMineModule() {
        super("AutoMine", "Automatically mines enemy blocks", ModuleCategory.WORLD);
    }

    @Override
    public String getModuleData() {
        if (debugConfig.getValue() && miningData != null && !miningData.getState().isReplaceable())
        {
            final float damage = miningData.damage;
            if (damage == Float.POSITIVE_INFINITY)
            {
                return "Remine";
            }
            else if (damage > 1.0f)
            {
                return "Break";
            }
            return String.format("%.1f", damage * 100.0f) + "%";
        }
        return super.getModuleData();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        miningData = null;
        if (mc.player != null)
        {
            Managers.INVENTORY.syncToClient();
        }
    }

    @EventListener
    public void onPlayerTick(PlayerTickEvent event) {
        if (mc.player.isCreative()) {
            return;
        }
        if (autoConfig.getValue()) {
            List<BlockPos> autoMines = getAutoMineTarget();
            if (!autoMines.isEmpty()) {
                BlockPos mineTarget = autoMines.get(0);
                if (miningData == null || !miningData.getPos().equals(mineTarget)) {
                    startMiningPos(mineTarget, Direction.UP);
                }
            }
        }
        if (miningData != null) {
            if (miningData.getState().isReplaceable() && miningData.getBreakState())
            {
                miningData.setBreakState(false);
            }

            double dist = mc.player.squaredDistanceTo(miningData.getPos().toCenterPos());
            if (dist > ((NumberConfig<?>) rangeConfig).getValueSq()) {
                Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, miningData.getPos(), Direction.DOWN));
                Managers.INVENTORY.syncToClient();
                miningData = null;
            }
        }
        if (failMiningData != null && failMiningData.getState().isAir()) {
            failMiningData = null;
        }
        checkMiningData(miningData);
        // checkMiningData(failMiningData);
    }

    @EventListener
    public void onAttackBlock(AttackBlockEvent event) {
        if (mc.player == null || mc.world == null || mc.player.isCreative()) {
            return;
        }
        event.cancel();
        startMiningPos(event.getPos(), event.getDirection());
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        if (mc.player.isCreative()) {
            return;
        }
        if (miningData != null && !miningData.getState().isReplaceable()) {
            BlockPos mining = miningData.getPos();
            VoxelShape outlineShape = miningData.getState().getOutlineShape(mc.world, mining);
            outlineShape = outlineShape.isEmpty() ? VoxelShapes.fullCube() : outlineShape;
            Box render1 = outlineShape.getBoundingBox();
            Box render = new Box(mining.getX() + render1.minX, mining.getY() + render1.minY,
                    mining.getZ() + render1.minZ, mining.getX() + render1.maxX,
                    mining.getY() + render1.maxY, mining.getZ() + render1.maxZ);
            Vec3d center = render.getCenter();
            float scale = MathHelper.clamp(miningData.getDamage() / miningData.getBreakSpeed(), 0, 1.0f);
            if (scale > 1.0f) {
                scale = 1.0f;
            }
            double dx = (render1.maxX - render1.minX) / 2.0;
            double dy = (render1.maxY - render1.minY) / 2.0;
            double dz = (render1.maxZ - render1.minZ) / 2.0;
            final Box scaled = new Box(center, center).expand(dx * scale, dy * scale, dz * scale);
            RenderManager.renderBox(event.getMatrices(), scaled,
                    miningData.getDamage() > (0.95f * miningData.getBreakSpeed()) ? 0x6000ff00 : 0x60ff0000);
            RenderManager.renderBoundingBox(event.getMatrices(), scaled,
                    2.5f, miningData.getDamage() > (0.95f * miningData.getBreakSpeed()) ? 0x6000ff00 : 0x60ff0000);
        }
//        if (failMiningData != null) {
//            BlockPos mining = failMiningData.getPos();
//            VoxelShape outlineShape = failMiningData.getState().getOutlineShape(mc.world, mining);
//            if (outlineShape.isEmpty()) {
//                return;
//            }
//            Box render1 = outlineShape.getBoundingBox();
//            Box render = new Box(mining.getX() + render1.minX, mining.getY() + render1.minY,
//                    mining.getZ() + render1.minZ, mining.getX() + render1.maxX,
//                    mining.getY() + render1.maxY, mining.getZ() + render1.maxZ);
//            Vec3d center = render.getCenter();
//            float scale = failMiningData.getDamage() / failMiningData.getBreakSpeed();
//            if (scale > 1.0f) {
//                scale = 1.0f;
//            }
//            double dx = (render1.maxX - render1.minX) / 2.0;
//            double dy = (render1.maxY - render1.minY) / 2.0;
//            double dz = (render1.maxZ - render1.minZ) / 2.0;
//            final Box scaled = new Box(center, center).expand(dx * scale, dy * scale, dz * scale);
//            RenderManager.renderBox(event.getMatrices(), scaled,
//                    failMiningData.getDamage() > (0.95f * failMiningData.getBreakSpeed()) ? 0x6000ff00 : 0x60ff0000);
//            RenderManager.renderBoundingBox(event.getMatrices(), scaled,
//                    2.5f, failMiningData.getDamage() > (0.95f * failMiningData.getBreakSpeed()) ? 0x6000ff00 : 0x60ff0000);
//        }
    }

    private void checkMiningData(MiningData data) {
        if (data == null) {
            return;
        }
        boolean failMine = data instanceof FailMiningData;
        float delta = Modules.SPEEDMINE.calcBlockBreakingDelta(data.getState(), mc.world, data.getPos());
        data.damageBlock(delta);
        if (data.getDamage() > data.getBreakSpeed() && !data.getState().isAir()) {
            if (mc.player.isUsingItem()) {
                return;
            }
            // data.setBreakState(true);
            if (rotateConfig.getValue()) {
                float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), data.getPos().toCenterPos());
                Managers.ROTATION.setRotationSilent(rotations[0], rotations[1], grimConfig.getValue());
            }
            if (failMine) {
                //
            }
            stopMiningPos(data.getPos(), data.getDirection());
            if (!remineConfig.getValue()) {
                miningData = null;
            }
        }
    }

    private void startMiningPos(BlockPos pos, Direction dir) {
        if (pos == null || dir == null) {
            return;
        }
        if (miningData == null || pos != miningData.getPos()) {
            miningData = new MiningData(pos, dir, speedConfig.getValue());

            // Grim does not check for slot changes, and it bases its original predicted block
            // break calculation on the ItemStack you have when you first send the START_DESTROY_BLOCK
            // https://github.com/GrimAnticheat/Grim/blob/2.0/src/main/java/ac/grim/grimac/checks/impl/misc/FastBreak.java#L76
            // https://github.com/GrimAnticheat/Grim/blob/2.0/src/main/java/ac/grim/grimac/checks/impl/misc/FastBreak.java#L98
            // To prevent a FastBreak flag, we should just swap quickly so grim can calculate and fuck off
            // This should also work on other AntiCheats that do the same thing
            final int slot = Modules.AUTO_TOOL.getBestToolNoFallback(mc.world.getBlockState(pos));
            if (slot != -1)
            {
                Managers.INVENTORY.setSlot(slot);
            }
            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, dir));
            Managers.INVENTORY.syncToClient();
            failMiningData = null;
        }
//        if (doubleBreakConfig.getValue()) {
//            if (miningData == null) {
//                miningData = new MiningData(pos, dir, 1.0f);
//                Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
//                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, dir));
//                stopMiningPos(pos, dir);
//                failMiningData = null;
//            } else if (pos != miningData.getPos()) {
//                failMiningData = new FailMiningData(pos, dir);
//                Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
//                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, dir));
//            }
//        }
    }

    private void stopMiningPos(BlockPos pos, Direction dir) {
        int slot = Modules.AUTO_TOOL.getBestToolNoFallback(mc.world.getBlockState(pos));
        int prev = mc.player.getInventory().selectedSlot;
        if (swapConfig.getValue() != Swap.OFF && slot != -1) {
            if (strictConfig.getValue()) {
                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                        slot + 36, prev, SlotActionType.SWAP, mc.player);
            } else {
                Managers.INVENTORY.setSlot(slot);
            }
        }
        Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, dir));
        if (grimConfig.getValue()) {
            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos.up(500), dir));
        }
        if (swapConfig.getValue() == Swap.SILENT && slot != -1) {
            if (strictConfig.getValue()) {
                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId,
                        slot + 36, prev, SlotActionType.SWAP, mc.player);
            } else {
                Managers.INVENTORY.syncToClient();
            }
        }
    }

    private List<BlockPos> getAutoMineTarget() {
        List<BlockPos> mineTargets = new ArrayList<>();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || Managers.SOCIAL.isFriend(player.getUuid())) {
                continue;
            }
            double dist = mc.player.distanceTo(player);
            if (dist > enemyRangeConfig.getValue()) {
                continue;
            }
            BlockPos pos = player.getBlockPos();
            if (!mc.world.isAir(pos)) {
                mineTargets.add(pos);
            }
            for (Direction direction : Direction.values()) {
                if (!direction.getAxis().isHorizontal()) {
                    continue;
                }
                BlockPos minePos = pos.offset(direction);
                BlockState state = mc.world.getBlockState(minePos);
                if (state.isAir()) {
                    continue;
                }
                mineTargets.add(minePos);
            }
        }
        return mineTargets;
    }

    public enum Swap {
        NORMAL,
        SILENT,
        OFF
    }

    public static class FailMiningData extends MiningData {

        public FailMiningData(BlockPos mining, Direction direction) {
            super(mining, direction, 1.0f);
        }
    }

    public static class MiningData {
        private final BlockPos mining;
        private final Direction direction;
        private float damage;
        private final float speed;
        private boolean breakState;

        public MiningData(BlockPos mining, Direction direction, float speed) {
            this.mining = mining;
            this.direction = direction;
            this.speed = speed;
            damage = 0.0f;
        }

        public float getBreakSpeed() {
            return speed;
        }

        public float getDamage() {
            return damage;
        }

        public void damageBlock(float damage) {
            this.damage += damage;
        }

        public BlockPos getPos() {
            return mining;
        }

        public Direction getDirection() {
            return direction;
        }

        public BlockState getState() {
            return mc.world.getBlockState(mining);
        }

        public void setBreakState(boolean breakState) {
            this.breakState = breakState;
        }

        public boolean getBreakState() {
            return breakState;
        }
    }
}