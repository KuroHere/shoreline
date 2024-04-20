package net.shoreline.client.impl.manager.player.interaction;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shoreline.client.Shoreline;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.Globals;

import java.util.Set;

/**
 * @author xgraza
 * @since 1.0
 */
public final class InteractionManager implements Globals
{
    public InteractionManager()
    {
        Shoreline.EVENT_HANDLER.subscribe(this);
    }

    public boolean placeBlock(final BlockPos pos,
                              final int slot,
                              final boolean strictDirection,
                              final boolean clientSwing,
                              final RotationCallback rotationCallback)
    {
        Direction direction = getInteractDirection(pos, strictDirection);
        if (isAirPlaceEnabled() && direction == null && !strictDirection)
        {
            // TODO: this should be not like this
            direction = Direction.UP;
        }
        final BlockPos neighbor = pos.offset(direction.getOpposite());
        return placeBlock(neighbor, direction, slot, clientSwing, rotationCallback);
    }

    public boolean placeBlock(final BlockPos pos,
                              final Direction direction,
                              final int slot,
                              final boolean clientSwing,
                              final RotationCallback rotationCallback)
    {
        // TODO: better fake hitVec
        return placeBlock(new BlockHitResult(pos.toCenterPos(), direction, pos, false),
                slot, clientSwing, rotationCallback);
    }

    public boolean placeBlock(final BlockHitResult hitResult,
                              final int slot,
                              final boolean clientSwing,
                              final RotationCallback rotationCallback)
    {
        final int previousSlot = mc.player.getInventory().selectedSlot;
        final boolean isSpoofing = slot != previousSlot;
        if (isSpoofing)
        {
            Managers.INVENTORY.setSlot(slot);
            // mc.player.getInventory().selectedSlot = slot;
        }

        final boolean isRotating = rotationCallback != null;
        if (isRotating)
        {
            rotationCallback.handleRotation(true);
        }

        final boolean result = placeBlockImmediately(hitResult, clientSwing);
        if (isRotating)
        {
            rotationCallback.handleRotation(false);
        }

        if (isSpoofing)
        {
            Managers.INVENTORY.syncToClient();
            //mc.player.getInventory().selectedSlot = previousSlot;
        }

        return result;
    }

    public boolean placeBlockImmediately(final BlockHitResult result, final boolean clientSwing)
    {
        final ActionResult actionResult = placeBlockInternally(result);
        if (actionResult.isAccepted() && actionResult.shouldSwingHand())
        {
            if (clientSwing)
            {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            else
            {
                Managers.NETWORK.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }
        }
        return actionResult.isAccepted();
    }

    private ActionResult placeBlockInternally(final BlockHitResult hitResult)
    {
        return mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
    }

    /**
     * @param blockPos
     * @param strictDirection
     * @return
     */
    public Direction getInteractDirection(final BlockPos blockPos, final boolean strictDirection)
    {
        final Set<Direction> ncpDirections = Managers.NCP.getPlaceDirectionsNCP(
                mc.player.getEyePos(), blockPos.toCenterPos());
        Direction interactDirection = null;
        for (final Direction direction : Direction.values())
        {
            final BlockState state = mc.world.getBlockState(blockPos.offset(direction));
            if (state.isAir() || !state.getFluidState().isEmpty())
            {
                continue;
            }
            if (strictDirection && !ncpDirections.contains(direction.getOpposite()))
            {
                continue;
            }
            interactDirection = direction;
            break;
        }
        if (interactDirection == null)
        {
            return null;
        }
        return interactDirection.getOpposite();
    }

    private boolean isAirPlaceEnabled()
    {
        return Modules.AIR_PLACE.isEnabled();
    }
}
