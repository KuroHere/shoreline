package com.caspian.client.impl.event.chat;

import com.caspian.client.api.event.Event;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ChatInputEvent extends Event
{
    private final int keycode;
    private final String chatText;

    public ChatInputEvent(int keycode, String chatText)
    {
        this.keycode = keycode;
        this.chatText = chatText;
    }

    /**
     *
     * @return
     */
    public int getKeyCode()
    {
        return keycode;
    }

    public String getChatText()
    {
        return chatText;
    }
}
