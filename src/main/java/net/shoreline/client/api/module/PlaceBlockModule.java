package net.shoreline.client.api.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
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
    protected void placeBlockResistant(BlockPos pos, boolean strictDirection) {
        int slot = getResistantBlockItem();
        if (slot == -1) {
            return;
        }
        placeBlock(slot, pos, strictDirection);
    }

    /**
     * @param slot
     * @param pos
     * @param strictDirection
     */
    protected void placeBlock(int slot, BlockPos pos, boolean strictDirection) {
        int prev = mc.player.getInventory().selectedSlot;
        if (prev != slot) {
            mc.player.getInventory().selectedSlot = slot;
            Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
        Managers.INTERACT.placeBlock(pos, strictDirection);
        if (prev != slot) {
            mc.player.getInventory().selectedSlot = prev;
            Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(prev));
        }
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
