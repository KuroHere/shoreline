package com.caspian.client.impl.module.world;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.impl.event.network.PlayerUpdateEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.world.BlastResistantBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NukerModule extends ToggleModule
{
    //
    Config<BreakMode> modeConfig = new EnumConfig<>("Mode", "The mode for " +
            "breaking blocks", BreakMode.SURVIVAL, BreakMode.values());
    Config<Timing> timingConfig = new EnumConfig<>("Timing", "The timing for " +
            "the break process", Timing.SEQUENTIAL, Timing.values());
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range to " +
            "break blocks", 0.1f, 5.0f, 7.0f);
    Config<Float> delayConfig = new NumberConfig<>("Delay", "The delay " +
            "between breaking blocks", 0.0f, 0.0f, 10.0f);
    Config<Boolean> flattenConfig = new BooleanConfig("Flatten", "Creates a " +
            "flat area when breaking blocks", false);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotates to " +
            "the current breaking position ", true);
    Config<Boolean> raytraceConfig = new BooleanConfig("Raytrace",
            "Raytraces to the current breaking position", true);
    Config<Boolean> strictDirectionConfig = new BooleanConfig("StrictDirection",
            "Clicks only on visible block faces", false);

    /**
     *
     */
    public NukerModule()
    {
        super("Nuker", "Destroys all blocks around the player", ModuleCategory.WORLD);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPlayerUpdate(PlayerUpdateEvent event)
    {
        if (timingConfig.getValue() == Timing.SEQUENTIAL)
        {
            BlockPos mineBlock = null;
            BlockState mineState = null;
            if (event.getStage() == EventStage.PRE)
            {
                mineBlock = getNukerBlock();
                mineState = mc.world.getBlockState(mineBlock);
            }
            else if (event.getStage() == EventStage.POST)
            {
                if (mineBlock != null && mineState != null
                        && isNukerBlock(mineState.getBlock()))
                {
                    Managers.INTERACT.breakBlock(mineBlock, Direction.UP);
                }
            }
        }
    }

    public BlockPos getNukerBlock()
    {
        BlockPos nukerBlock = null;
        float range = rangeConfig.getValue();
        for (float x = range; x >= -range; x--)
        {
            for (float y = range; y >= -range; y--)
            {
                for (float z = range; z >= -range; z--)
                {
                    BlockPos pos = BlockPos.ofFloored(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.isAir() || state.getBlock() instanceof FluidBlock
                            || mc.player.squaredDistanceTo(pos.toCenterPos()) > range * range
                            || !isNukerBlock(state.getBlock()))
                    {
                        continue;
                    }
                    nukerBlock = pos;
                }
            }
        }
        return nukerBlock;
    }

    /**
     *
     * @param block
     * @return
     */
    public boolean isNukerBlock(Block block)
    {
        return BlastResistantBlocks.isBreakable(block);
    }

    public enum Timing
    {
        SEQUENTIAL, VANILLA
    }

    public enum BreakMode
    {
        SURVIVAL, CREATIVE
    }
}
