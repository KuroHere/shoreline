package com.caspian.client.util.world;

import com.caspian.client.util.Globals;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkManager;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BlockUtil implements Globals
{
    /**
     *
     * @param x
     * @param z
     * @return
     */
    public static boolean isBlockLoaded(double x, double z)
    {
        ChunkManager chunkManager = mc.world.getChunkManager();
        if (chunkManager != null)
        {
            return chunkManager.isChunkLoaded(ChunkSectionPos.getSectionCoord(x),
                    ChunkSectionPos.getSectionCoord(z));
        }
        return false;
    }
}
