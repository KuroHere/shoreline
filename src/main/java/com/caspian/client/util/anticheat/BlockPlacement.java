package com.caspian.client.util.anticheat;

import com.caspian.client.init.Managers;
import com.caspian.client.util.Globals;
import com.caspian.client.util.world.SneakBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
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
public class BlockPlacement implements Globals
{
    /**
     *
     * @param pos
     * @param strictDirection
     */
    public static void placeBlock(BlockPos pos, boolean strictDirection)
    {
        placeBlock(pos, Hand.MAIN_HAND, strictDirection);
    }

    /**
     *
     * @param pos
     */
    public static void placeBlock(BlockPos pos, Hand hand)
    {
        placeBlock(pos, hand, false);
    }

    /**
     *
     * @param pos
     */
    public static void placeBlock(BlockPos pos)
    {
        placeBlock(pos, Hand.MAIN_HAND, false);
    }

    /**
     *
     * @param pos
     * @param hand
     * @param strictDirection
     */
    public static void placeBlock(BlockPos pos, Hand hand, boolean strictDirection)
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
}
