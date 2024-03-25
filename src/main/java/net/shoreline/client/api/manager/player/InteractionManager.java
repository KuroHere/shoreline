package net.shoreline.client.api.manager.player;

import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.Globals;
import net.shoreline.client.util.player.RotationUtil;
import net.shoreline.client.util.world.SneakBlocks;

import java.util.Set;

/**
 * @author linus
 * @since 1.0
 */
public class InteractionManager implements Globals {
    //
    private boolean blockCancel;
    // TODO: usingItem impl
    private boolean breakingBlock, usingItem;

    /**
     *
     */
    public InteractionManager() {
        Shoreline.EVENT_HANDLER.subscribe(this);
    }

    /**
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event) {
        if (mc.player != null && mc.world != null) {
            if (event.getPacket() instanceof PlayerActionC2SPacket packet) {
                if (packet.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
                    breakingBlock = true;
                } else if (packet.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK
                        || packet.getAction() == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
                    breakingBlock = false;
                }
            } else if (event.getPacket() instanceof PlayerInteractItemC2SPacket) {
                usingItem = true;
            } else if (event.getPacket() instanceof PlayerInteractBlockC2SPacket) {
                usingItem = true;
            }
        }
    }

    /**
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() == EventStage.PRE) {
            blockCancel = false;
        } else if (event.getStage() == EventStage.POST) {
            if (!blockCancel && mc.interactionManager != null) {
                mc.interactionManager.cancelBlockBreaking();
            }
        }
    }

    public float[] placeBlock(BlockPos pos, boolean rotate, boolean strictDirection) {
        return placeBlock(pos, Hand.MAIN_HAND, rotate, strictDirection);
    }

    public float[] placeBlock(BlockPos pos, Hand hand, boolean rotate) {
        return placeBlock(pos, hand, rotate, false);
    }

    public float[] placeBlock(BlockPos pos) {
        return placeBlock(pos, Hand.MAIN_HAND, false);
    }

    /**
     * @param pos
     * @param hand
     * @param strictDirection
     */
    public float[] placeBlock(BlockPos pos, Hand hand, boolean rotate, boolean strictDirection) {
        BlockState state = mc.world.getBlockState(pos);
        Direction sideHit = getInteractDirection(pos, strictDirection);
        if (!state.isReplaceable() || sideHit == null) {
            return null;
        }
        BlockPos offsetPos = pos.offset(sideHit.getOpposite());
        Vec3d hitVec = Vec3d.ofCenter(offsetPos);
        BlockState state1 = mc.world.getBlockState(offsetPos);
        boolean sneaking = !mc.player.isSneaking() && SneakBlocks.isSneakBlock(state1.getBlock());
        if (sneaking) {
            Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                    ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
        // sideHit = sideHit.getOpposite();
        hitVec = hitVec.add(sideHit.getOffsetX() * 0.5, sideHit.getOffsetY() * 0.5, sideHit.getOffsetZ() * 0.5);
        float[] rotations = RotationUtil.getRotationsTo(mc.player.getEyePos(), hitVec);
        if (rotate) {
            Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                    rotations[0], rotations[1], mc.player.isOnGround()));
        }
        BlockHitResult result = new BlockHitResult(hitVec, sideHit, offsetPos, false);
        Managers.NETWORK.sendSequencedPacket(id ->
                new PlayerInteractBlockC2SPacket(hand, result, id));
        // Managers.NETWORK.sendPacket(new HandSwingC2SPacket(hand));
        mc.player.swingHand(hand);
        if (sneaking) {
            Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                    ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        return rotations;
    }

    /**
     * @param blockPos
     * @param strictDirection
     * @return
     */
    private Direction getInteractDirection(BlockPos blockPos, boolean strictDirection) {
        Set<Direction> ncpDirections = Managers.NCP.getPlaceDirectionsNCP(mc.player.getEyePos(), blockPos.toCenterPos());
        Direction interactDirection = null;
        for (Direction dir : Direction.values()) {
            BlockPos pos1 = blockPos.offset(dir);
            Direction opposite = dir.getOpposite();
            BlockState state = mc.world.getBlockState(pos1);
            //
            if (state.isAir() || !state.getFluidState().isEmpty()) {
                continue;
            }
            if (strictDirection && !ncpDirections.contains(opposite)) {
                continue;
            }
            interactDirection = opposite;
            break;
        }
        return interactDirection;
    }

    /**
     * @param pos
     * @param direction
     */
    public void breakBlock(BlockPos pos, Direction direction) {
        breakBlock(pos, direction, true);
    }

    /**
     * @param pos
     * @param direction
     * @param swing
     */
    public void breakBlock(BlockPos pos, Direction direction, boolean swing) {
        // Create new instance
        BlockPos breakPos = pos;
        if (mc.interactionManager.isBreakingBlock()) {
            mc.interactionManager.updateBlockBreakingProgress(breakPos, direction);
        } else {
            mc.interactionManager.attackBlock(breakPos, direction);
        }
        if (swing) {
            mc.player.swingHand(Hand.MAIN_HAND);
        } else {
            mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        }
        blockCancel = true;
    }

    /**
     * @return
     */
    public boolean isBreakingBlock() {
        return breakingBlock;
    }
}
