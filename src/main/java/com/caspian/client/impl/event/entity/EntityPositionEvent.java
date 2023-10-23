package com.caspian.client.impl.event.entity;

import com.caspian.client.api.event.Event;
import net.minecraft.util.math.Box;

public class EntityPositionEvent extends Event
{
    private final Box updatePos;

    public EntityPositionEvent(Box updatePos)
    {
        this.updatePos = updatePos;
    }

    public Box getUpdatePos()
    {
        return updatePos;
    }
}
