package com.caspian.client.api.handler;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class InventoryHandler
{
    // The serverside selected hotbar slot. This will determine the held item
    // serverside
    private int slot;
    
    /**
     *
     *
     */
    public InventoryHandler()
    {
        slot = -1;
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
}
