package com.caspian.client.impl.event;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

/**
 *
 */
@Cancelable
public class FramerateLimitEvent extends Event
{
    private int framerateLimit;

    public void setFramerateLimit(int framerateLimit)
    {
        this.framerateLimit = framerateLimit;
    }

    public int getFramerateLimit()
    {
        return framerateLimit;
    }
}
