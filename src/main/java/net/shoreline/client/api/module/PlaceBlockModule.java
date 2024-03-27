package net.shoreline.client.api.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.shoreline.client.impl.module.combat.SurroundModule;
import net.shoreline.client.init.Managers;

/**
 * @author linus
 * @see SurroundModule
 * @since 1.0
 */
public class PlaceBlockModule extends RotationModule {
    // TODO: series of blocks
    public PlaceBlockModule(String name, String desc, ModuleCategory category) {
        super(name, desc, category);
    }

    protected void placeBlockResistant(BlockPos pos) {
        placeBlockResistant(pos, false);
    }

    /**
     * @param pos
     * @param strictDirection
     */
    protected float[] placeBlockResistant(BlockPos pos, boolean rotate, boolean strictDirection) {
        int slot = getResistantBlockItem();
        if (slot == -1) {
            return null;
        }
        return placeBlock(slot, pos, rotate, strictDirection);
    }

    protected float[] placeBlockResistant(BlockPos pos, boolean strictDirection) {
        return placeBlockResistant(pos, false, strictDirection);
    }

    protected float[] placeBlock(int slot, BlockPos pos, boolean strictDirection) {
        return placeBlock(slot, pos, false, strictDirection);
    }

    /**
     * @param slot
     * @param pos
     * @param strictDirection
     */
    protected float[] placeBlock(int slot, BlockPos pos, boolean rotate, boolean strictDirection) {
        int prev = mc.player.getInventory().selectedSlot;
        if (prev != slot) {
            mc.player.getInventory().selectedSlot = slot;
            Managers.INVENTORY.setSlot(slot);
        }
        float[] rotations = Managers.INTERACT.placeBlock(pos, rotate, strictDirection);
        if (prev != slot) {
            mc.player.getInventory().selectedSlot = prev;
            Managers.INVENTORY.setSlot(prev);
        }
        return rotations;
    }

    /**
     * @return
     */
    public int getResistantBlockItem() {
        int slot = getBlockItemSlot(Blocks.OBSIDIAN);
        if (slot == -1) {
            slot = getBlockItemSlot(Blocks.CRYING_OBSIDIAN);
        }
        if (slot == -1) {
            return getBlockItemSlot(Blocks.ENDER_CHEST);
        }
        return slot;
    }

    public int getBlockItemSlot(Block block) {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem block1
                    && block1.getBlock() == block) {
                slot = i;
                break;
            }
        }
        return slot;
    }
}
