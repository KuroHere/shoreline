package net.shoreline.client.impl.module.world;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.BlockPlacerModule;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.chat.ChatUtil;

/**
 * @author xgraza
 * @since 04/13/24
 */
public final class ScaffoldModule extends BlockPlacerModule
{
    public ScaffoldModule()
    {
        super("Scaffold", "Rapidly places blocks under your feet", ModuleCategory.WORLD);
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();

        if (mc.player != null)
        {
            Managers.INVENTORY.syncToClient();
        }
    }

    @EventListener
    public void onUpdate(final PlayerTickEvent event)
    {
        final BlockData data = getBlockData();
        if (data == null)
        {
            return;
        }

        final int blockSlot = getBlockSlot();
        if (blockSlot == -1)
        {
            return;
        }

        // TODO: needs raytracing for Grim (linus removed it :( )
        placeBlock(blockSlot, data.pos(), data.direction(), true, grimConfig.getValue());
    }

    private int getBlockSlot()
    {
        int slot = -1;
        int count = 0;
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            final int stackCount = stack.getCount();
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem)
            {
                if (stackCount > count || slot == -1)
                {
                    slot = i;
                    count = stackCount;
                }
            }
        }
        return slot;
    }

    private BlockData getBlockData()
    {
        final BlockPos pos = new BlockPos(MathHelper.floor(mc.player.getX()), MathHelper.floor(mc.player.getY()), MathHelper.floor(mc.player.getZ())).down();

        for (final Direction direction : Direction.values())
        {
            final BlockPos neighbor = pos.offset(direction);
            if (!mc.world.getBlockState(neighbor).isReplaceable())
            {
                return new BlockData(neighbor, direction.getOpposite());
            }
        }

        for (final Direction direction : Direction.values())
        {
            final BlockPos neighbor = pos.offset(direction);
            if (mc.world.getBlockState(neighbor).isReplaceable())
            {
                for (final Direction direction1 : Direction.values())
                {
                    final BlockPos neighbor1 = neighbor.offset(direction1);
                    if (!mc.world.getBlockState(neighbor1).isReplaceable())
                    {
                        return new BlockData(neighbor1, direction1.getOpposite());
                    }
                }
            }
        }

        return null;
    }

    private record BlockData(BlockPos pos, Direction direction){};
}
