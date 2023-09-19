package com.caspian.client.api.manager.player;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.mixin.accessor.AccessorClientPlayerInteractionManager;
import com.caspian.client.util.Globals;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class InventoryManager implements Globals
{
    // The serverside selected hotbar slot. This will determine the held item
    // serverside
    private int slot;
    
    /**
     *
     *
     */
    public InventoryManager()
    {
        slot = -1;
        Caspian.EVENT_HANDLER.subscribe(this);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet)
        {
            slot = packet.getSelectedSlot();
        }
    }

    /**
     *
     *
     * @return
     */
    public int getServerSlot()
    {
        return slot;
    }
    
    /**
     *
     *
     * @param slot
     */
    public void pickupSlot(final int slot)
    {
        click(slot, SlotActionType.PICKUP, false);
    }

    /**
     *
     *
     * @param slot
     */
    public void throwSlot(final int slot)
    {
        click(slot, SlotActionType.THROW, false);
    }
    
    /**
     *
     *
     * @param slot
     * @param type
     * @param tick
     */
    private void click(final int slot,
                       final SlotActionType type,
                       final boolean tick)
    {
        mc.interactionManager.clickSlot(0, slot, 0, type, mc.player);
        if (tick)
        {
            syncSelectedSlot();
        }
    }

    /**
     *
     */
    public void syncSelectedSlot()
    {
        ((AccessorClientPlayerInteractionManager) mc.interactionManager).hookSyncSelectedSlot();
    }

    /**
     *
     *
     * @return
     */
    public ItemStack getServerItem()
    {
        if (mc.player != null)
        {
            return mc.player.getInventory().getStack(getServerSlot());
        }
        return null;
    }
}
