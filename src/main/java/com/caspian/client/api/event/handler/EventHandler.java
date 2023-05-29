package com.caspian.client.api.event.handler;

import com.caspian.client.api.event.Event;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.event.listener.Listener;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Event
 * @see EventListener
 * @see Listener
 */
public class EventHandler
{
    // Active subscriber cache. Used to check if a class is already
    // subscribed to this EventHandler.
    private final Set<Object> subscribers =
            Collections.synchronizedSet(new HashSet<>());

    // Map of events and their associated listeners. All listeners in a class
    // will be added when the class is subscribed to this EventHandler.
    private final Map<Object, Set<Listener>> listeners =
            Collections.synchronizedMap(new ConcurrentHashMap<>());

    /**
     * Subscribes a {@link Object} to the EventHandler and adds all
     * {@link EventListener} in the class to the active listener map.
     *
     * @param obj The subscriber object
     */
    public void subscribe(Object obj)
    {
        subscribers.add(obj);
        for (Method method : obj.getClass().getMethods())
        {
            method.trySetAccessible();
            if (method.isAnnotationPresent(EventListener.class))
            {
                if (method.getReturnType() == Void.TYPE)
                {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 1 && Event.class.isAssignableFrom(params[0]))
                    {
                        Set<Listener> active = listeners.computeIfAbsent(params[0],
                                v -> new HashSet<>());
                        active.add(new Listener(method, obj));
                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param obj
     */
    public void unsubscribe(Object obj)
    {
        unsubscribe(obj.getClass());
    }

    /**
     * Unsubscribes the subscriber {@link Class} and all associated
     * {@link EventListener} from the listener map.
     *
     * @param clazz The subscriber class
     */
    public void unsubscribe(Class<?> clazz)
    {
        if (subscribers.remove(clazz))
        {
            listeners.values().forEach(set ->
                    set.removeIf(l -> l.getSubscriber() == clazz));
            listeners.entrySet().removeIf(e -> e.getValue().isEmpty());
        }
    }

    /**
     * Runs {@link Listener#invoke(Event)} on all active {@link Listener} for
     * the param {@link Event}
     *
     * @param event The event to dispatch listeners
     * @return <tt>true</tt> if {@link Event#isCanceled()} is <tt>true</tt>
     */
    public boolean dispatch(Event event)
    {
        Set<Listener> active = listeners.get(event.getClass());
        // if there are no items to dispatch to, just early return
        if (active == null || active.isEmpty())
        {
            return false;
        }
        for (Listener listener : active)
        {
            listener.invoke(event);
        }
        return event.isCanceled();
    }
}
