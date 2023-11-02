package com.caspian.client.impl.module.misc;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiSpamModule extends ToggleModule
{
    //
    private final Map<UUID, String> lastPlayerMessages = new HashMap<>();

    /**
     *
     */
    public AntiSpamModule()
    {
        super("AntiSpam", "Prevents players from spamming the game chat",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player == null)
        {
            return;
        }
        if (event.getPacket() instanceof ChatMessageS2CPacket packet)
        {
            final UUID sender = packet.sender();
            final String chatMessage = packet.body().content();
            String lastMessage = lastPlayerMessages.get(sender);
            if (chatMessage.equalsIgnoreCase(lastMessage))
            {
                event.cancel();
            }
            else if (lastMessage != null)
            {
                lastPlayerMessages.replace(sender, chatMessage);
            }
            else
            {
                lastPlayerMessages.put(sender, chatMessage);
            }
        }
    }
}
