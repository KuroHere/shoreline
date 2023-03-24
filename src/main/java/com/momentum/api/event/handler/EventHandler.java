package com.momentum.api.event.handler;

import com.momentum.api.event.Event;
import com.momentum.api.event.Listener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EventHandler implementation for {@link Event} events (also known as an <i>EventBus</i>).
 * Manages all {@link Listener} listeners for an event.
 *
 * @author linus
 * @since 03/20/2023
 */
public class EventHandler implements IEventHandler {

    // set of listeners
    // concurrent for thread safety
    private final Map<Class<Event>, Set<Listener>> listeners =
            new ConcurrentHashMap<>();

    /**
     * Subscribes a given {@link Listener} event listener
     * to the handler
     *
     * @param l The event listener
     * @throws NullPointerException if the listener is <tt>null</tt>
     */
    @Override
    public void subscribe(Listener l) {

        // null check
        if (l == null)
        {
            throw new NullPointerException("EventHandler does not support null listeners");
        }

        // active listeners
        Set<Listener> active = listeners.computeIfAbsent(
                l.getEventClass(), target -> new HashSet<>());

        // add to listener set
        active.add(l);
    }

    /**
     * Removes a given {@link Listener} event listener
     * from the handler
     *
     * @param l The event listener
     * @throws NullPointerException if the listener is <tt>null</tt>
     */
    @Override
    public void unsubscribe(Listener l) {

        // null check
        if (l == null)
        {
            throw new NullPointerException("EventHandler does not support null listeners");
        }

        // active listeners
        Set<Listener> active = listeners.get(l.getEventClass());

        // remove from listener set
        active.remove(l);
        if (active.isEmpty())
        {

            // remove from list
            listeners.remove(l.getEventClass());
        }
    }

    /**
     * Removes all listeners in the handler. Handler will
     * be empty after calling <tt>clear</tt>
     */
    @Override
    public void clear() {
        listeners.clear();
    }

    /**
     * Invokes all {@link Listener} listeners associated
     * with an event
     *
     * @param e The event
     * @throws NullPointerException if the event is </t>null</tt>
     */
    @Override
    public boolean dispatch(Event e) {

        // null check
        if (e == null)
        {
            throw new NullPointerException("EventHandler does not dispatch null events");
        }

        // set of all associated listeners
        Set<Listener> active = listeners.get(e.getClass());

        // invoke all listeners
        for (Listener l : active)
        {

            // invoke listener
            l.invoke(e);
        }

        // return true if the event was cancelled
        return e.isCanceled();
    }
}
