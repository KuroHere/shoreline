package com.momentum.api.util.chat;

import com.momentum.api.util.Globals;
import net.minecraft.text.Text;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ChatUtil implements Globals
{
    /**
     * Sends a message in the {@link net.minecraft.client.gui.hud.ChatHud}
     * which is not visible to others
     *
     * @param msg The message
     */
    public static void clientSendMessage(String msg)
    {
        mc.inGameHud.getChatHud().addMessage(Text.literal(msg));
    }

    /**
     * Sends a message in the {@link net.minecraft.client.network.ClientPlayNetworkHandler}
     * which is visible to others on a server
     *
     * @param msg The message
     */
    public static void serverSendMessage(String msg)
    {
        mc.player.networkHandler.sendChatMessage(msg);
    }
}
