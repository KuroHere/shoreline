package com.momentum.api.event;

import com.momentum.api.event.handler.EventHandler;
import com.momentum.api.event.listener.Listener;

/**
 * Event that can be listened to by a {@link Listener}. Processed by
 * the {@link EventHandler}.
 *
 * @author linus
 * @since 03/20/2023
 */
public abstract class Event implements ICancelable
{
    // cancelled state. Indicates if the event should run.
    private boolean cancelled;

    /**
     * Sets this event's cancelled state
     *
     * @param in The cancelled state
     */
    @Override
    public void setCancelled(boolean in)
    {
        // update cancelled state
        if (isCancelable())
        {
            cancelled = in;
        }
    }

    /**
     * Gets this event's cancelled state
     *
     * @return The cancelled state
     */
    @Override
    public boolean isCanceled()
    {
        return cancelled;
    }

    /**
     * Gets this event's cancelable state
     *
     * @return The cancelable state
     */
    @Override
    public abstract boolean isCancelable();
}
