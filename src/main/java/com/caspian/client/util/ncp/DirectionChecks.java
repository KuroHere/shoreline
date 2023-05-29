package com.caspian.client.util.ncp;

import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class DirectionChecks
{
    /**
     *
     *
     * @param xdiff
     * @param ydiff
     * @return
     */
    public static List<Direction> getInteractableDirections(final int xdiff,
                                                            final int ydiff,
                                                            final int zdiff)
    {
        // directly from NCP src
        final List<Direction> dirs = new ArrayList<>(6);
        if (ydiff == 0) 
        {
            dirs.add(Direction.UP);
            dirs.add(Direction.DOWN);
        }
        else 
        {
            dirs.add(ydiff > 0 ? Direction.UP : Direction.DOWN);
        }
        if (xdiff != 0) 
        {
            dirs.add(xdiff > 0 ? Direction.EAST : Direction.WEST);
        }
        if (zdiff != 0)
        {
            dirs.add(zdiff > 0 ? Direction.SOUTH : Direction.NORTH);
        }
        return dirs;
    }
}
