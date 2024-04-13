package net.shoreline.client.impl.module.world;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
import net.shoreline.client.util.player.RayCastUtil;
import net.shoreline.client.util.player.RotationUtil;

/**
 * @author xgraza
 * @since 04/13/24
 */
public final class ScaffoldModule extends BlockPlacerModule
{
    Config<Boolean> tower = new BooleanConfig("Tower", "Goes up faster when holding down space", true);

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
    }

    @EventListener
    public void onUpdate(final PlayerTickEvent event)
    {
        final BlockData data = getBlockData();
        if (data == null)
        {
            return;
        }

        final int blockSlot = getBlockSlot();
        if (blockSlot == -1)
        {
            return;
        }

        Vec3d hitVec = data.pos().toCenterPos();
        BlockHitResult hitResult = new BlockHitResult(hitVec, data.direction(), data.pos(), false);
        if (grimConfig.getValue())
        {
            final GrimPlaceResult placeResult = getRotationAnglesFor(data);
            if (placeResult == null)
            {
                return;
            }

            hitResult = placeResult.result();
            float[] angles = placeResult.angles();
            Managers.ROTATION.setRotationSilent(angles[0], angles[1], true);
        }
        else
        {
            float[] angles = RotationUtil.getRotationsTo(mc.player.getEyePos(), hitVec);
            setRotation(angles[0], angles[1]);
        }

        if (Managers.INVENTORY.getServerSlot() != blockSlot)
        {
            Managers.INVENTORY.setSlot(blockSlot);
        }

        final ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
        if (result.isAccepted())
        {
            if (result.shouldSwingHand())
            {
                mc.player.swingHand(Hand.MAIN_HAND);
            }

            // if someone uses this on grim and complains "it doesnt work!!!" im gonna find their house
            if (tower.getValue() && mc.options.jumpKey.isPressed())
            {
                final Vec3d velocity = mc.player.getVelocity();
                final double velocityY = velocity.y;
                if ((mc.player.isOnGround() || velocityY < 0.1) || velocityY <= 0.16477328182606651)
                {
                    mc.player.setVelocity(velocity.x, 0.42f, velocity.z);
                }
            }

            if (grimConfig.getValue())
            {
                Managers.ROTATION.setRotationSilentSync(true);
            }
        }

        Managers.INVENTORY.syncToClient();
    }

    private GrimPlaceResult getRotationAnglesFor(final BlockData data)
    {
        // insane 10/10 code (will NOT lag your game!!!)
        // TODO: make actual rotations instead of this retarded hardcode
        float[] angles = new float[2];
        angles[0] = Math.round(((mc.player.getYaw() - 180) + 1) / 45) * 45;

        for (float yaw = 80.0f; yaw <= 90.0f; yaw += 0.5f)
        {
            angles[1] = yaw;
            final HitResult result = RayCastUtil.rayCast(4.0, angles);
            if (result instanceof BlockHitResult hitResult && hitResult.getBlockPos().equals(data.pos()) && hitResult.getSide() == data.direction())
            {
                return new GrimPlaceResult(hitResult, angles);
            }
        }

        return null;
    }

    private int getBlockSlot()
    {
        int slot = -1;
        int count = 0;
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            final int stackCount = stack.getCount();
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem)
            {
                if (stackCount > count || slot == -1)
                {
                    slot = i;
                    count = stackCount;
                }
            }
        }
        return slot;
    }

    private BlockData getBlockData()
    {
        final BlockPos pos = new BlockPos(MathHelper.floor(mc.player.getX()), MathHelper.floor(mc.player.getY()), MathHelper.floor(mc.player.getZ())).down();

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

    private record BlockData(BlockPos pos, Direction direction)
    {

    }

    private record GrimPlaceResult(BlockHitResult result, float[] angles)
    {

    }
}
