package com.caspian.client.api.module;

import com.caspian.client.init.Managers;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see com.caspian.client.impl.module.combat.SurroundModule
 */
public class PlaceBlockModule extends ToggleModule
{

    public PlaceBlockModule(String name, String desc, ModuleCategory category)
    {
        super(name, desc, category);
    }

    public PlaceBlockModule(String name, String desc, ModuleCategory category,
                            Integer keycode)
    {
        super(name, desc, category, keycode);

    }

    protected void placeBlocks(List<BlockPos> placements, boolean rotate)
    {
        placeBlocks(placements, rotate, false);
    }

    /**
     *
     * @param placements
     * @param rotate
     * @param strictDirection
     */
    protected void placeBlocks(List<BlockPos> placements, boolean rotate,
                               boolean strictDirection)
    {
        int slot = getResistantBlockItem();
        if (slot == -1)
        {
            return;
        }
        int prev = mc.player.getInventory().selectedSlot;
        if (prev != slot)
        {
            mc.player.getInventory().selectedSlot = slot;
            Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
        for (BlockPos p : placements)
        {
            Managers.INTERACT.placeBlock(p, rotate, strictDirection);
        }
        if (prev != slot)
        {
            mc.player.getInventory().selectedSlot = prev;
            Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(prev));
        }
    }

    protected void placeBlock(BlockPos pos, boolean rotate)
    {
        placeBlock(pos, rotate, false);
    }

    /**
     *
     * @param pos
     * @param rotate
     * @param strictDirection
     */
    protected void placeBlock(BlockPos pos, boolean rotate, boolean strictDirection)
    {
        int slot = getResistantBlockItem();
        if (slot == -1)
        {
            return;
        }
        int prev = mc.player.getInventory().selectedSlot;
        if (prev != slot)
        {
            mc.player.getInventory().selectedSlot = slot;
            Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
        //
        Managers.INTERACT.placeBlock(pos, rotate, strictDirection);
        if (prev != slot)
        {
            mc.player.getInventory().selectedSlot = prev;
            Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(prev));
        }
    }


    /**
     *
     * @return
     */
    public int getResistantBlockItem()
    {
        int slot = -1;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem block
                    && block.getBlock() == Blocks.OBSIDIAN)
            {
                slot = i;
                break;
            }
        }
        if (slot == -1)
        {
            for (int i = 0; i < 9; i++)
            {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.getItem() instanceof BlockItem block
                        && block.getBlock() == Blocks.ENDER_CHEST)
                {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }
}
