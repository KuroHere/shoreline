package com.caspian.impl.event.network;

import com.caspian.api.event.Cancelable;
import com.caspian.api.event.Event;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 */
@Cancelable
public class AttackBlockEvent extends Event
{
    //
    private final BlockPos pos;
    private final Direction direction;

    /**
     *
     *
     * @param pos
     * @param direction
     */
    public AttackBlockEvent(BlockPos pos, Direction direction)
    {
        this.pos = pos;
        this.direction = direction;
    }

    /**
     *
     *
     * @return
     */
    public BlockPos getPos()
    {
        return pos;
    }

    /**
     *
     *
     * @return
     */
    public Direction getDirection()
    {
        return direction;
    }
}
