package com.caspian.client.util.chat;

import com.caspian.client.util.Globals;
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
     * @param message The message
     */
    public static void clientSendMessage(String message)
    {
        mc.inGameHud.getChatHud().addMessage(Text.literal(message));
    }

    /**
     * Sends a message in the {@link net.minecraft.client.network.ClientPlayNetworkHandler}
     * which is visible to others on a server
     *
     * @param message The message
     */
    public static void serverSendMessage(String message)
    {
        if (mc.player != null)
        {
            mc.player.networkHandler.sendChatMessage(message);
        }
    }
}
