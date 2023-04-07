package com.momentum.impl.event;

import com.momentum.api.event.Event;

/**
 *
 */
public class ChatMessageEvent extends Event
{
    // chat message content
    private final String content;

    /**
     * Default constructor
     *
     * @param content Message content
     */
    public ChatMessageEvent(String content)
    {
        this.content = content;
    }

    /**
     * Returns the chat message content
     *
     * @return The chat message content
     */
    public String getContent()
    {
        // trim
        return content.trim();
    }

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
}
