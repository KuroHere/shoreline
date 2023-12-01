package com.caspian.client.util.world;

import com.caspian.client.util.Globals;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkManager;

import java.util.List;

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
     * @return
     */
    public static List<BlockEntity> blockEntities()
    {
        return null;
    }

    /**
     *
     * @param pos
     * @return
     */
    public static boolean isBlockAccessible(BlockPos pos)
    {
        return mc.world.isAir(pos) && !mc.world.isAir(pos.add(0, -1, 0))
                && mc.world.isAir(pos.add(0, 1, 0)) && mc.world.isAir(pos.add(0, 2, 0));
    }


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
