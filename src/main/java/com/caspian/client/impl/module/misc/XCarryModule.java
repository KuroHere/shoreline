package com.caspian.client.impl.module.misc;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class XCarryModule extends ToggleModule
{
    /**
     *
     */
    public XCarryModule()
    {
        super("XCarry", "Allow player to carry items in the crafting slots",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (event.getPacket() instanceof CloseHandledScreenC2SPacket packet
                && packet.getSyncId() == mc.player.playerScreenHandler.syncId)
        {
            event.cancel();
        }
    }
}
