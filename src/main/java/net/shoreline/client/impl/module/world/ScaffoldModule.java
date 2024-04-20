package net.shoreline.client.impl.module.world;

import net.minecraft.item.BlockItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.BlockPlacerModule;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.player.MovementUtil;
import net.shoreline.client.util.player.RayCastUtil;
import net.shoreline.client.util.player.RotationUtil;

/**
 * @author xgraza
 * @since 04/13/24
 */
public final class ScaffoldModule extends BlockPlacerModule
{
    Config<Boolean> towerConfig = new BooleanConfig("Tower", "Goes up faster when holding down space", true);
    Config<Boolean> keepYConfig = new BooleanConfig("KeepY", "Keeps your Y level", false);

    private int posY = -1;

    public ScaffoldModule()
    {
        super("Scaffold", "Rapidly places blocks under your feet", ModuleCategory.WORLD);
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();

        if (mc.player != null)
        {
            Managers.INVENTORY.syncToClient();
        }
        posY = -1;
    }

    @EventListener
    public void onUpdate(final PlayerTickEvent event)
    {
        final BlockData data = getBlockData();
        if (data == null)
        {
            return;
        }

        final int blockSlot = getSlot(
                (stack) -> stack.getItem() instanceof BlockItem);
        if (blockSlot == -1)
        {
            return;
        }

        if (grimConfig.getValue())
        {
            getRotationAnglesFor(data);
            if (data.getHitResult() == null)
            {
                return;
            }
        }
        else
        {
            data.setAngles(RotationUtil.getRotationsTo(mc.player.getEyePos(), data.getHitResult().getPos()));
        }

        final boolean result = Managers.INTERACT.placeBlock(data.getHitResult(), blockSlot, false, (state) ->
        {
            float[] angles = data.getAngles();
            if (angles == null)
            {
                return;
            }
            if (grimConfig.getValue())
            {
                if (state)
                {
                    Managers.ROTATION.setRotationSilent(angles[0], angles[1], true);
                }
                else
                {
                    Managers.ROTATION.setRotationSilentSync(true);
                }
            }
            else if (state)
            {
                setRotation(angles[0], angles[1]);
            }
        });

        if (result)
        {
            // if someone uses this on grim and complains "it doesnt work!!!" im gonna find their house
            if (towerConfig.getValue() && mc.options.jumpKey.isPressed())
            {
                final Vec3d velocity = mc.player.getVelocity();
                final double velocityY = velocity.y;
                if ((mc.player.isOnGround() || velocityY < 0.1) || velocityY <= 0.16477328182606651)
                {
                    mc.player.setVelocity(velocity.x, 0.42f, velocity.z);
                }
            }
        }
    }

    private void getRotationAnglesFor(final BlockData data)
    {
        final float rotationYaw = MovementUtil.getYawOffset(
            mc.player.getYaw() - 180);

        if (data.getSide() == Direction.UP)
        {
            final float[] angles = { rotationYaw, 90.0f };
            final HitResult result = RayCastUtil.rayCast(4.0, angles);
            if (result instanceof BlockHitResult hitResult
                && hitResult.getBlockPos().equals(data.getPos())
                && hitResult.getSide() == data.getSide())
            {
                data.setHitResult(hitResult);
                data.setAngles(angles);
                return;
            }
        }

        for (float yaw = rotationYaw - 45; yaw <= rotationYaw + 45; yaw += 1)
        {
            for (float pitch = 75; pitch <= 90; pitch += 1)
            {
                final float[] angles = { yaw, pitch };
                final HitResult result = RayCastUtil.rayCast(4.0, angles);
                if (result instanceof BlockHitResult hitResult
                        && hitResult.getBlockPos().equals(data.getPos())
                        && hitResult.getSide() == data.getSide())
                {
                    data.setHitResult(hitResult);
                    data.setAngles(angles);
                    return;
                }
            }
        }

        data.setHitResult(null);
        data.setAngles(null);
    }

    private BlockData getBlockData()
    {
        if (!keepYConfig.getValue() || mc.player.isOnGround())
        {
            posY = MathHelper.floor(mc.player.getY());
        }

        final BlockPos pos = new BlockPos(MathHelper.floor(mc.player.getX()), posY, MathHelper.floor(mc.player.getZ())).down();

        for (final Direction direction : Direction.values())
        {
            final BlockPos neighbor = pos.offset(direction);
            if (!mc.world.getBlockState(neighbor).isReplaceable())
            {
                return new BlockData(neighbor, direction.getOpposite());
            }
        }

        for (final Direction direction : Direction.values())
        {
            final BlockPos neighbor = pos.offset(direction);
            if (mc.world.getBlockState(neighbor).isReplaceable())
            {
                for (final Direction direction1 : Direction.values())
                {
                    final BlockPos neighbor1 = neighbor.offset(direction1);
                    if (!mc.world.getBlockState(neighbor1).isReplaceable())
                    {
                        return new BlockData(neighbor1, direction1.getOpposite());
                    }
                }
            }
        }

        return null;
    }

    private static class BlockData
    {
        private BlockHitResult hitResult;
        private float[] angles;

        public BlockData(final BlockPos pos, final Direction direction)
        {
            this(new BlockHitResult(pos.toCenterPos(), direction, pos, false), null);
        }

        public BlockData(final BlockHitResult hitResult, final float[] angles)
        {
            this.hitResult = hitResult;
            this.angles = angles;
        }

        public BlockHitResult getHitResult()
        {
            return hitResult;
        }

        public void setHitResult(BlockHitResult hitResult)
        {
            this.hitResult = hitResult;
        }

        public BlockPos getPos()
        {
            return hitResult.getBlockPos();
        }

        public Direction getSide()
        {
            return hitResult.getSide();
        }

        public float[] getAngles()
        {
            return angles;
        }

        public void setAngles(float[] angles)
        {
            this.angles = angles;
        }
    }

    private record GrimPlaceResult(BlockHitResult result, float[] angles)
    {

    }
}
