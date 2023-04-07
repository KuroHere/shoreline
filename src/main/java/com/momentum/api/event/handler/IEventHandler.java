package com.momentum.api.event.handler;

import com.momentum.api.event.Event;
import com.momentum.api.event.listener.Listener;

/**
 * Handler for {@link Event} events (also known as an <i>EventBus</i>). Manages all {@link Listener}
 * listeners for an event.
 *
 * @author linus
 * @since 03/20/2023
 */
public interface IEventHandler
{
    /**
     * Subscribes a given event {@link Listener} to the handler
     *
     * @param l The event listener
     * @throws NullPointerException if the listener is null
     */
    void subscribe(Listener l);

    /**
     * Removes a given event {@link Listener} from the handler
     *
     * @param l The event listener
     * @throws NullPointerException if the listener is null
     */
    void unsubscribe(Listener l);

    /**
     * Removes all listeners in the handler. Handler will
     * be empty after calling <tt>clear</tt>
     */
    void clear();

    /**
     * Invokes all {@link Listener} associated with an {@link Event}
     *
     * @param e The event
     * @return Whether the event was canceled
     * @throws NullPointerException if the event is null
     */
    boolean dispatch(Event e);
}
