package com.caspian.client.impl.event.gui.chat;

import com.caspian.client.api.event.Event;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ChatInputEvent extends Event
{
    private final String chatText;

    public ChatInputEvent(String chatText)
    {
        this.chatText = chatText;
    }

    public String getChatText()
    {
        return chatText;
    }
}
