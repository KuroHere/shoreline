package com.momentum.impl.event;

import com.momentum.api.event.Event;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Called in {@link net.minecraft.client.gui.screen.ChatScreen#render(MatrixStack, int, int, float)}
 *
 * @author linus
 * @since 1.0
 */
public class ChatTextRenderEvent extends Event
{
    // chat text
    private String chatText;

    /**
     * Gets this event's cancelable state
     *
     * @return The cancelable state
     */
    @Override
    public boolean isCancelable()
    {
        return true;
    }

    /**
     * Sets the chat text, will be rendered on top of vanilla chat text
     *
     * @param in The new chat text
     */
    public void setChatText(String in)
    {
        chatText = in;
    }

    /**
     * Returns the chat text
     *
     * @return The chat text
     */
    public String getChatText()
    {
        return chatText;
    }
}
