package com.caspian.impl.event.chat;

import com.caspian.api.event.Event;

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
