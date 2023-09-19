package com.caspian.client.util.world;

import com.caspian.client.util.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public class BlastResistantBlocks implements Globals
{
    // All blocks that are resistant to explosions
    private static final Set<Block> BLAST_RESISTANT = Set.of(
            Blocks.OBSIDIAN,
            Blocks.ANVIL,
            Blocks.ENCHANTING_TABLE,
            Blocks.ENDER_CHEST,
            Blocks.BEACON
    );
    // All blocks that are unbreakable with tools in survival mode
    private static final Set<Block> UNBREAKABLE = Set.of(
            Blocks.BEDROCK,
            Blocks.COMMAND_BLOCK,
            Blocks.CHAIN_COMMAND_BLOCK,
            Blocks.END_PORTAL_FRAME,
            Blocks.BARRIER
    );

    /**
     *
     * @param pos
     * @return
     */
    public static boolean isBreakable(BlockPos pos)
    {
        return isBreakable(mc.world.getBlockState(pos).getBlock());
    }

    /**
     *
     * @param block
     * @return
     */
    public static boolean isBreakable(Block block)
    {
        return !UNBREAKABLE.contains(block);
    }

    /**
     *
     * @param pos
     * @return
     */
    public static boolean isUnbreakable(BlockPos pos)
    {
        return isUnbreakable(mc.world.getBlockState(pos).getBlock());
    }

    /**
     *
     * @param block
     * @return
     */
    public static boolean isUnbreakable(Block block)
    {
        return UNBREAKABLE.contains(block);
    }

    /**
     *
     * @param pos
     * @return
     */
    public static boolean isBlastResistant(BlockPos pos)
    {
        return isBlastResistant(mc.world.getBlockState(pos).getBlock());
    }

    /**
     *
     * @param block
     * @return
     */
    public static boolean isBlastResistant(Block block)
    {
        return BLAST_RESISTANT.contains(block);
    }
}
