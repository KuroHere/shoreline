package com.caspian.util.world;

import com.caspian.util.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.Arrays;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SneakBlocks implements Globals
{
    //
    private static final List<Block> sneakBlocks;

    /**
     *
     *
     * @return
     */
    public static boolean isSneakBlock(BlockState state)
    {
        return isSneakBlock(state.getBlock());
    }

    /**
     *
     *
     * @return
     */
    public static boolean isSneakBlock(Block block)
    {
        return sneakBlocks.contains(block);
    }

    static
    {
        sneakBlocks = Arrays.asList(
                Blocks.CHEST,
                Blocks.ENDER_CHEST,
                Blocks.TRAPPED_CHEST
        );
    }
}
