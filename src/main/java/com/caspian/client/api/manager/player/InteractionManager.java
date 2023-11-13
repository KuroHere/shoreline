package com.caspian.client.api.manager.player;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.manager.player.rotation.RotationPriority;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.Globals;
import com.caspian.client.util.anticheat.DirectionChecks;
import com.caspian.client.util.player.RotationUtil;
import com.caspian.client.util.world.SneakBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class InteractionManager implements Globals
{
    // TODO: usingItem impl
    private boolean breakingBlock, usingItem;

    /**
     *
     *
     */
    public InteractionManager()
    {
        Caspian.EVENT_HANDLER.subscribe(this);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof PlayerActionC2SPacket packet)
            {
                if (packet.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK)
                {
                    breakingBlock = true;
                }
                else if (packet.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK
                        || packet.getAction() == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK)
                {
                    breakingBlock = false;
                }
            }
            else if (event.getPacket() instanceof PlayerInteractItemC2SPacket)
            {
                usingItem = true;
            }
            else if (event.getPacket() instanceof PlayerInteractBlockC2SPacket)
            {
                usingItem = true;
            }
        }
    }

    /**
     *
     * @param pos
     * @param rotate
     * @param strictDirection
     */
    public void placeBlock(BlockPos pos, boolean rotate,
                                  boolean strictDirection)
    {
        placeBlock(pos, Hand.MAIN_HAND, rotate, strictDirection);
    }

    /**
     *
     * @param pos
     * @param hand
     * @param rotate
     */
    public void placeBlock(BlockPos pos, Hand hand, boolean rotate)
    {
        placeBlock(pos, hand, rotate, false);
    }

    /**
     *
     * @param pos
     * @param rotate
     */
    public void placeBlock(BlockPos pos, boolean rotate)
    {
        placeBlock(pos, Hand.MAIN_HAND, rotate);
    }

    /**
     *
     * @param pos
     */
    public void placeBlock(BlockPos pos)
    {
        placeBlock(pos, false);
    }

    /**
     *
     * @param pos
     * @param hand
     * @param rotate
     * @param strictDirection
     */
    public void placeBlock(BlockPos pos, Hand hand, boolean rotate,
                           boolean strictDirection)
    {
        final List<Entity> entities = mc.world.getOtherEntities(null,
                new Box(pos), e -> e != null && !(e instanceof ExperienceOrbEntity));
        if (!mc.world.isAir(pos) || !entities.isEmpty())
        {
            return;
        }
        for (Direction dir : Direction.values())
        {
            final BlockPos blockPos = Managers.POSITION.getBlockPos();
            int x = blockPos.getX();
            int y = (int) Math.floor(Managers.POSITION.getY()
                    + mc.player.getStandingEyeHeight());
            int z = blockPos.getZ();
            BlockPos off = pos.offset(dir);
            Set<Direction> strictDirections = DirectionChecks.getPlaceDirectionsNCP(
                    x, y, z, off.getX(), off.getY(), off.getZ());
            if (strictDirection && !strictDirections.contains(dir.getOpposite()))
            {
                continue;
            }
            BlockState state = mc.world.getBlockState(off);
            boolean sneaking = SneakBlocks.isSneakBlock(state.getBlock())
                    && !Managers.POSITION.isSneaking();
            if (sneaking)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            }
            // Place block
            Vec3d pos1 = Vec3d.ofCenter(blockPos).add(dir.getOffsetX() * 0.5,
                    dir.getOffsetY() * 0.5, dir.getOffsetZ() * 0.5);
            final BlockHitResult result = new BlockHitResult(pos1,
                    dir.getOpposite(), off, false);
            if (rotate)
            {
                float[] angles = RotationUtil.getRotationsTo(mc.player.getEyePos(), result.getPos());
                Managers.ROTATION.setRotation(null, RotationPriority.HIGHEST,
                        angles[0], angles[1]);
            }
            Managers.NETWORK.sendSequencedPacket(id -> new PlayerInteractBlockC2SPacket(hand, result, id));
            Managers.NETWORK.sendPacket(new HandSwingC2SPacket(hand));
            if (sneaking)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            }
            return;
        }
    }

    /**
     *
     *
     * @return
     */
    public boolean isBreakingBlock()
    {
        return breakingBlock;
    }
}
