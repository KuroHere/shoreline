package net.shoreline.client.impl.module.movement;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.entity.player.PlayerJumpEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.impl.event.network.PlayerUpdateEvent;
import net.shoreline.client.impl.event.world.BlockCollisionEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.mixin.accessor.AccessorKeyBinding;
import net.shoreline.client.mixin.accessor.AccessorPlayerMoveC2SPacket;
import net.shoreline.client.util.string.EnumFormatter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShapes;
import net.shoreline.client.util.Globals;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class JesusModule extends ToggleModule
{
    //
    Config<JesusMode> modeConfig = new EnumConfig<>("Mode", "The mode for " +
            "walking on water", JesusMode.SOLID, JesusMode.values());
    Config<Boolean> strictConfig = new BooleanConfig("Strict", "NCP Updated " +
            "bypass for floating offsets", false,
            () -> modeConfig.getValue() == JesusMode.SOLID);
    //
    private int floatTimer = 1000;
    private boolean fluidState;
    //
    private double floatOffset;

    /**
     *
     */
    public JesusModule()
    {
        super("Jesus", "Allow player to walk on water", ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @return
     */
    @Override
    public String getModuleData()
    {
        return EnumFormatter.formatEnum(modeConfig.getValue());
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        floatOffset = 0.0;
        //
        floatTimer = 1000;
        KeyBinding.setKeyPressed(((AccessorKeyBinding) Globals.mc.options.jumpKey).getBoundKey(), false);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onBlockCollision(BlockCollisionEvent event)
    {
        BlockState state = event.getState();
        if (Modules.FLIGHT.isEnabled() || Modules.PACKET_FLY.isEnabled()
                || Globals.mc.player.isSpectator() || Globals.mc.player.isOnFire()
                || state.getFluidState().isEmpty())
        {
            return;
        }
        if (modeConfig.getValue() != JesusMode.DOLPHIN
                && ((state.getBlock() == Blocks.WATER
                | state.getFluidState().getFluid() == Fluids.WATER)
                || state.getBlock() == Blocks.LAVA))
        {
            event.cancel();
            event.setVoxelShape(VoxelShapes.fullCube());
            if (Globals.mc.player.getVehicle() != null)
            {
                event.setVoxelShape(VoxelShapes.cuboid(new Box(0.0, 0.0, 0.0, 1.0,
                        0.949999988079071, 1.0)));
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPlayerJump(PlayerJumpEvent event)
    {
        if (!isInFluid() && isOnFluid())
        {
            event.cancel();
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            if (Modules.FLIGHT.isEnabled() || Modules.PACKET_FLY.isEnabled())
            {
                return;
            }
            if (modeConfig.getValue() == JesusMode.SOLID)
            {
                if (isInFluid() || Globals.mc.player.fallDistance > 3.0f
                        || Globals.mc.player.isSneaking())
                {
                    // floatOffset = 0.0;
                }
                if (!Globals.mc.options.sneakKey.isPressed() && !Globals.mc.options.jumpKey.isPressed())
                {
                    if (isInFluid())
                    {
                        floatTimer = 0;
                        Managers.MOVEMENT.setMotionY(0.11);
                        return;
                    }
                    if (floatTimer == 0)
                    {
                        Managers.MOVEMENT.setMotionY(0.30);
                    }
                    else if (floatTimer == 1)
                    {
                        Managers.MOVEMENT.setMotionY(0.0);
                    }
                    floatTimer++;
                }
            }
            else if (modeConfig.getValue() == JesusMode.DOLPHIN && isInFluid()
                    && !Globals.mc.options.sneakKey.isPressed() && !Globals.mc.options.jumpKey.isPressed())
            {
                KeyBinding.setKeyPressed(((AccessorKeyBinding) Globals.mc.options.jumpKey).getBoundKey(), true);
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPlayerUpdate(PlayerUpdateEvent event)
    {
        if (Modules.FLIGHT.isEnabled() || Modules.PACKET_FLY.isEnabled())
        {
            return;
        }
        if (event.getStage() == EventStage.PRE
                && modeConfig.getValue() == JesusMode.TRAMPOLINE)
        {
            boolean inFluid = getFluidBlockInBB(Globals.mc.player.getBoundingBox()) != null;
            if (inFluid && !Globals.mc.player.isSneaking())
            {
                Globals.mc.player.setOnGround(false);
            }
            Block block = Globals.mc.world.getBlockState(new BlockPos((int) Math.floor(Globals.mc.player.getX()),
                    (int) Math.floor(Globals.mc.player.getY()),
                    (int) Math.floor(Globals.mc.player.getZ()))).getBlock();
            if (fluidState && !Globals.mc.player.getAbilities().flying && !Globals.mc.player.isTouchingWater())
            {
                if (Globals.mc.player.getVelocity().y < -0.3 || Globals.mc.player.isOnGround()
                        || Globals.mc.player.isHoldingOntoLadder())
                {
                    fluidState = false;
                    return;
                }
                Managers.MOVEMENT.setMotionY(Globals.mc.player.getVelocity().y / 0.9800000190734863 + 0.08);
                Managers.MOVEMENT.setMotionY(Globals.mc.player.getVelocity().y - 0.03120000000005);
            }
            if (isInFluid())
            {
                Managers.MOVEMENT.setMotionY(0.1);
            }
            if (!isInFluid() && block instanceof FluidBlock
                    && Globals.mc.player.getVelocity().y < 0.2)
            {
                Managers.MOVEMENT.setMotionY(0.5);
                fluidState = true;
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (event.isClientPacket() || Globals.mc.player == null || Globals.mc.getNetworkHandler() == null
                || Globals.mc.player.age <= 20 || Modules.FLIGHT.isEnabled()
                || Modules.PACKET_FLY.isEnabled())
        {
            return;
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet
                && packet.changesPosition()
                && modeConfig.getValue() == JesusMode.SOLID && !isInFluid()
                && isOnFluid() && Globals.mc.player.fallDistance <= 3.0f)
        {
            double y = packet.getY(Globals.mc.player.getY());
            if (!strictConfig.getValue())
            {
                floatOffset = Globals.mc.player.age % 2 == 0 ? 0.0 : 0.05;
            }
            ((AccessorPlayerMoveC2SPacket) packet).hookSetY(y - floatOffset);
            if (strictConfig.getValue())
            {
                floatOffset += 0.12;
                if (floatOffset > 0.4)
                {
                    floatOffset = 0.2;
                }
            }
        }
    }

    public boolean isInFluid()
    {
        return Globals.mc.player.isTouchingWater() || Globals.mc.player.isInLava();
    }

    /**
     *
     * @param box
     * @return
     */
    public BlockState getFluidBlockInBB(Box box)
    {
        return getFluidBlockInBB(MathHelper.floor(box.minY - 0.2));
    }

    /**
     *
     * @param minY
     * @return
     */
    public BlockState getFluidBlockInBB(int minY)
    {
        for(int i = MathHelper.floor(Globals.mc.player.getBoundingBox().minX); i < MathHelper.ceil(Globals.mc.player.getBoundingBox().maxX); i++)
        {
            for(int j = MathHelper.floor(Globals.mc.player.getBoundingBox().minZ); j < MathHelper.ceil(Globals.mc.player.getBoundingBox().maxZ); j++)
            {
                BlockState state = Globals.mc.world.getBlockState(new BlockPos(i, minY, j));
                if (state.getBlock() instanceof FluidBlock)
                {
                    return state;
                }
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public boolean isOnFluid()
    {
        if (Globals.mc.player.fallDistance >= 3.0f)
        {
            return false;
        }
        final Box bb = Globals.mc.player.getVehicle() != null ?
                Globals.mc.player.getVehicle().getBoundingBox().contract(0.0, 0.0, 0.0)
                        .offset(0.0, -0.05000000074505806, 0.0) :
                Globals.mc.player.getBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0,
                        -0.05000000074505806, 0.0);
        boolean onLiquid = false;
        int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0); x++)
        {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0); z++)
            {
                final Block block = Globals.mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR)
                {
                    if (!(block instanceof FluidBlock))
                    {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }

    public enum JesusMode
    {
        SOLID,
        DOLPHIN,
        TRAMPOLINE
    }
}
