package com.caspian.client.util.chat;

import com.caspian.client.util.Globals;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ChatUtil implements Globals
{
    //
    private static final String PREFIX = "§7[§fCaspian§7] §f";

    /**
     * Sends a message in the {@link net.minecraft.client.gui.hud.ChatHud}
     * which is not visible to others
     *
     * @param message The message
     */
    public static void clientSendMessage(String message)
    {
        mc.inGameHud.getChatHud().addMessage(Text.of(PREFIX + message), null, null);
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
            mc.player.networkHandler.sendChatMessage(PREFIX + message);
        }
    }

    /**
     *
     * @param message
     */
    public static void error(String message)
    {
        clientSendMessage(Formatting.RED + message);
    }
}
