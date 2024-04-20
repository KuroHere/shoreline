package net.shoreline.client.api.module;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.impl.module.combat.SurroundModule;

import java.util.Set;
import java.util.function.Predicate;

/**
 * @author linus
 * @see SurroundModule
 * @since 1.0
 */
public class BlockPlacerModule extends RotationModule
{
    private static final Set<Block> RESISTANT_BLOCKS = new ReferenceOpenHashSet<>(Set.of(
            Blocks.OBSIDIAN,
            Blocks.CRYING_OBSIDIAN,
            Blocks.ENDER_CHEST
    ));

    protected Config<Boolean> strictDirectionConfig = new BooleanConfig("StrictDirection", "Places on visible sides only", false);
    protected Config<Boolean> grimConfig = new BooleanConfig("Grim", "Places using grim instant rotations", false);

    public BlockPlacerModule(String name, String desc, ModuleCategory category) {
        super(name, desc, category);
        register(strictDirectionConfig, grimConfig);
    }

    public BlockPlacerModule(String name, String desc, ModuleCategory category, int rotationPriority) {
        super(name, desc, category, rotationPriority);
        register(strictDirectionConfig, grimConfig);
    }

    protected int getSlot(final Predicate<ItemStack> filter)
    {
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (!itemStack.isEmpty() && filter.test(itemStack))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return
     */
    protected int getResistantBlockItem()
    {
        for (final Block type : RESISTANT_BLOCKS)
        {
            final int slot = getBlockItemSlot(type);
            if (slot != -1)
            {
                return slot;
            }
        }
        return -1;
    }

    protected int getBlockItemSlot(final Block block)
    {
        for (int i = 0; i < 9; i++)
        {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() == block)
            {
                return i;
            }
        }
        return -1;
    }
}
