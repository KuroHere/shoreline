package net.shoreline.client.impl.module.world;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.network.AttackBlockEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.impl.manager.player.rotation.Rotation;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.player.RotationUtil;

import static net.shoreline.client.impl.module.world.AutoToolModule.getBestTool;

/**
 * @author xgraza
 * @since 1.0
 */
public final class AutoMineModule extends ToggleModule
{
    Config<Double> rangeConfig = new NumberConfig<>("Range", "How far away you should allow breaking", 1.0, 4.5, 6.0);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "If to rotate to the block you're breaking", true);
    Config<Boolean> grimConfig = new BooleanConfig("Grim", "If to use Silent Grim rotate", true, rotateConfig::getValue);
    Config<Boolean> fastRemineConfig = new BooleanConfig("FastRemine", "If to instantly remine a block", true);
    Config<Boolean> doubleBreakConfig = new BooleanConfig("DoubleBreak", "If to break two blocks at once", false);

    private BlockBreakData data;
    private float blockDamage;
    private boolean canRemine, sendBreak;

    public AutoMineModule()
    {
        super("AutoMine", "Automatically mines enemy blocks", ModuleCategory.WORLD);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (data != null && (canRemine || blockDamage > 0.0f))
        {
            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                    data.pos(), data.direction()));
        }

        if (mc.player == null)
        {
            Managers.INVENTORY.syncToClient();
        }

        data = null;
        blockDamage = 0.0f;
        canRemine = false;
        sendBreak = false;
    }

    @EventListener
    public void onAttackBlock(final AttackBlockEvent event)
    {
        final BlockState state = event.getState();
        if (state.isAir() || state.getBlock().getHardness() == -1.0f)
        {
            return;
        }

        event.cancel();

        final BlockPos pos = event.getPos();
        final Direction direction = event.getDirection();

        if (data != null)
        {
            if (data.pos().equals(pos) && data.direction() == direction)
            {
                canRemine = true;
            }
            else
            {
                Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                        data.pos(), data.direction()));
                data = null;
            }
        }

        data = new BlockBreakData(event.getPos(), event.getDirection());
        startMining();
        if (canRemine)
        {
            stopMining();
        }
    }

    @EventListener
    public void onPacketInbound(final PacketEvent.Inbound event)
    {
        if (event.getPacket() instanceof BlockUpdateS2CPacket packet)
        {
            if (data == null)
            {
                return;
            }
            if (data.pos().equals(packet.getPos())
                    && data.getBlockState().isReplaceable()
                    && !packet.getState().isReplaceable())
            {
                startMining();
            }
        }
    }

    @EventListener
    public void onPlayerTick(final PlayerTickEvent event)
    {
        if (data == null || data.getBlockState().isReplaceable())
        {
            if (data != null && data.pos().getSquaredDistance(mc.player.getX(), mc.player.getY(), mc.player.getZ()) > ((NumberConfig<Double>) rangeConfig).getValueSq())
            {
                Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                        data.pos(), data.direction()));

                blockDamage = 0.0f;
                data = null;
                canRemine = false;
                sendBreak = false;
                return;
            }
            if (!fastRemineConfig.getValue())
            {
                blockDamage = 0.0f;
            }
            return;
        }

        blockDamage += Modules.SPEEDMINE.calcBlockBreakingDelta(
                data.getBlockState(), mc.world, data.pos());
        if (blockDamage >= 1.0f && (!sendBreak || canRemine))
        {
            sendBreak = true;
            stopMining();
        }
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        if (data == null || mc.player.isCreative())
        {
            return;
        }
        final BlockPos pos = data.pos();
        final VoxelShape outlineShape = data.getBlockState().getOutlineShape(
                mc.world, pos);
        if (outlineShape.isEmpty())
        {
            return;
        }
        Box render1 = outlineShape.getBoundingBox();
        Box render = new Box(pos.getX() + render1.minX, pos.getY() + render1.minY,
                pos.getZ() + render1.minZ, pos.getX() + render1.maxX,
                pos.getY() + render1.maxY, pos.getZ() + render1.maxZ);
        Vec3d center = render.getCenter();
        float scale = blockDamage;
        if (scale > 1.0f) {
            scale = 1.0f;
        }
        double dx = (render1.maxX - render1.minX) / 2.0;
        double dy = (render1.maxY - render1.minY) / 2.0;
        double dz = (render1.maxZ - render1.minZ) / 2.0;
        final Box scaled = new Box(center, center).expand(dx * scale, dy * scale, dz * scale);
        RenderManager.renderBox(event.getMatrices(), scaled,
                blockDamage >= 0.95f ? 0x6000ff00 : 0x60ff0000);
        RenderManager.renderBoundingBox(event.getMatrices(), scaled,
                2.5f, blockDamage >= 0.95f ? 0x6000ff00 : 0x60ff0000);
    }

    private void startMining()
    {
        // If we have not started to break, send break block packet
        if (blockDamage == 0.0f)
        {
            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                    data.pos(), data.direction()));
        }
    }

    private void stopMining()
    {
        if (data != null)
        {
            final int slot = getBestTool(data.getBlockState());
            if (blockDamage >= 0.95f || canRemine)
            {
                //Managers.INVENTORY.setSlot(slot);

                if (rotateConfig.getValue())
                {
                    final float[] angles = RotationUtil.getRotationsTo(
                            mc.player.getEyePos(), data.pos().toCenterPos());
                    if (grimConfig.getValue())
                    {
                        Managers.ROTATION.setRotationSilent(angles[0], angles[1], true);
                    }
                    else
                    {
                        Managers.ROTATION.setRotation(new Rotation(0, angles[0], angles[1]));
                    }
                }

                if (canRemine)
                {
                    Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                            data.pos().up(500), data.direction()));
                }
                Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                        data.pos(), data.direction()));

                //Managers.INVENTORY.syncToClient();
            }
        }
    }

    private record BlockBreakData(BlockPos pos, Direction direction)
    {
        private BlockState getBlockState()
        {
            return mc.world.getBlockState(pos);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof BlockBreakData d))
            {
                return false;
            }
            return d.pos().equals(pos) && d.direction() == direction;
        }
    }
}
