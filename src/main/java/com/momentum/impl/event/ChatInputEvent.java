package com.momentum.impl.event;

import com.momentum.api.event.Event;

/**
 * Called during {@link net.minecraft.client.gui.screen.ChatScreen#keyPressed(int, int, int)}
 *
 * @author linus
 * @since 1.0
 */
public class ChatInputEvent extends Event
{
    // input keycode
    public final int keycode;

    // chat field text
    public final String chatText;

    /**
     * Initializes the ChatInputEvent with the input keycode and text in the
     * chat when the event is invoked
     *
     * @param keycode The inputted GLFW keycode
     * @param chatText The text in the chat field
     */
    public ChatInputEvent(int keycode, String chatText)
    {
        this.keycode = keycode;
        this.chatText = chatText;
    }

    /**
     * Gets this event's cancelable state
     *
     * @return The cancelable state
     */
    @Override
    public boolean isCancelable()
    {
        return false;
    }

    /**
     * Returns the input keycode
     *
     * @return The input keycode
     */
    public int getKeycode()
    {
        return keycode;
    }

    /**
     * Returns the chat field text
     *
     * @return The chat field text
     */
    public String getChatText()
    {
        return chatText;
    }
}
