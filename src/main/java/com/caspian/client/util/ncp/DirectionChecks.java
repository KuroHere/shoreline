package com.caspian.client.util.ncp;

import com.caspian.client.util.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.*;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class DirectionChecks implements Globals
{
    /**
     *
     *
     * @param x
     * @param y
     * @param z
     * @param dx
     * @param dy
     * @param dz
     * @param exposed
     * @return
     */
    public static Set<Direction> getInteractableDirections(final int x,
                                                           final int y,
                                                           final int z,
                                                           final int dx,
                                                           final int dy,
                                                           final int dz,
                                                           final boolean exposed)
    {
        // directly from NCP src
        final BlockPos pos = new BlockPos(dx, dy, dz);
        final Vec3d center = pos.toCenterPos();
        final BlockState state = mc.world.getBlockState(pos);
        final double xdiff = x - center.getX();
        final double ydiff = y - center.getY();
        final double zdiff = z - center.getZ();
        final Set<Direction> dirs = new HashSet<>(6);
        if (xdiff < -0.5)
        {
            dirs.add(Direction.WEST);
        }
        else if (xdiff > 0.5)
        {
            dirs.add(Direction.EAST);
        }
        else if (state.isFullCube(mc.world, pos))
        {
            dirs.add(Direction.WEST);
            dirs.add(Direction.EAST);
        }
        if (ydiff < -0.5)
        {
            dirs.add(Direction.DOWN);
        }
        else if (ydiff > 0.5)
        {
            dirs.add(Direction.UP);
        }
        else
        {
            dirs.add(Direction.DOWN);
            dirs.add(Direction.UP);
        }
        if (zdiff < -0.5)
        {
            dirs.add(Direction.NORTH);
        }
        else if (zdiff > 0.5)
        {
            dirs.add(Direction.SOUTH);
        }
        else if (state.isFullCube(mc.world, pos))
        {
            dirs.add(Direction.NORTH);
            dirs.add(Direction.SOUTH);
        }
        if (exposed)
        {
            // deepcopy
            final Set<Direction> interacts = new HashSet<>(dirs);
            dirs.clear();
            for (Direction d : interacts)
            {
                final BlockPos off = pos.offset(d);
                final BlockState state1 = mc.world.getBlockState(off);
                if (!state1.isFullCube(mc.world, off))
                {
                    dirs.add(d);
                }
            }
        }
        return dirs;
    }
}
