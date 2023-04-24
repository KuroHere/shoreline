package com.caspian.impl.event.world;

import com.caspian.api.event.StageEvent;
import net.minecraft.util.math.ChunkPos;

public class ChunkLoadEvent extends StageEvent
{
    private final ChunkPos pos;

    public ChunkLoadEvent(ChunkPos pos)
    {
        this.pos = pos;
    }

    public ChunkPos getPos()
    {
        return pos;
    }
}
