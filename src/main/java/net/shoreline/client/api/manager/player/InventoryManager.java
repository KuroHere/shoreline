package net.shoreline.client.api.manager.player;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.mixin.accessor.AccessorClientPlayerInteractionManager;
import net.shoreline.client.util.Globals;

import java.util.ArrayList;

/**
 * @author linus
 * @since 1.0
 */
public class InventoryManager implements Globals {

    // The serverside selected hotbar slot. This will determine the held item
    // serverside
    private int slot;

    /**
     *
     */
    public InventoryManager() {
        slot = -1;
        Shoreline.EVENT_HANDLER.subscribe(this);
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
            slot = packet.getSelectedSlot();
        }
    }

    //
    public void closeScreen() {
        Managers.NETWORK.sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
    }

    /**
     * @param slot
     */
    public void pickupSlot(final int slot) {
        click(slot, 0, SlotActionType.PICKUP);
    }

    public void quickMove(final int slot) {
        click(slot, 0, SlotActionType.QUICK_MOVE);
    }

    /**
     * @param slot
     */
    public void throwSlot(final int slot) {
        click(slot, 0, SlotActionType.THROW);
    }

    /**
     * @param slot
     * @param button
     * @param type
     */
    private void click(int slot, int button, SlotActionType type) {
        ScreenHandler screenHandler = mc.player.currentScreenHandler;
        DefaultedList<Slot> defaultedList = screenHandler.slots;
        int i = defaultedList.size();
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(i);
        for (Slot slot1 : defaultedList) {
            list.add(slot1.getStack().copy());
        }
        screenHandler.onSlotClick(slot, button, type, mc.player);
        Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2;
            ItemStack itemStack = list.get(j);
            if (ItemStack.areEqual(itemStack, itemStack2 = defaultedList.get(j).getStack())) continue;
            int2ObjectMap.put(j, itemStack2.copy());
        }
        mc.player.networkHandler.sendPacket(new ClickSlotC2SPacket(screenHandler.syncId, screenHandler.getRevision(), slot, button, type, screenHandler.getCursorStack().copy(), int2ObjectMap));
    }

    //
    public void syncSelectedSlot() {
        ((AccessorClientPlayerInteractionManager) mc.interactionManager).hookSyncSelectedSlot();
    }

    /**
     * @param item
     * @return
     */
    public int count(Item item) {
        int itemCount = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack slot = mc.player.getInventory().getStack(i);
            if (slot.getItem() == item) {
                itemCount += slot.getCount();
            }
        }
        return itemCount;
    }

    /**
     * @return
     */
    public int getServerSlot() {
        return slot;
    }

    /**
     * @return
     */
    public ItemStack getServerItem() {
        if (mc.player != null) {
            return mc.player.getInventory().getStack(getServerSlot());
        }
        return null;
    }
}
