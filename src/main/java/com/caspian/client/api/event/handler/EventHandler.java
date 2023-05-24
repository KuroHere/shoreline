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
    private final Set<Class<?>> subscribers =
            Collections.synchronizedSet(new HashSet<>());

    // Map of events and their associated listeners. All listeners in a class
    // will be added when the class is subscribed to this EventHandler.
    private final Map<Class<?>, Set<Listener>> listeners =
            Collections.synchronizedMap(new ConcurrentHashMap<>());

    /**
     *
     *
     * @param obj
     */
    public void subscribe(Object obj)
    {
        subscribe(obj.getClass());
    }

    /**
     * Subscribes a {@link Class} to the EventHandler and adds all
     * {@link EventListener} in the class to the active listener map.
     *
     * @param clazz The subscriber class
     */
    public void subscribe(Class<?> clazz)
    {
        subscribers.add(clazz);
        for (Method method : clazz.getMethods())
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
                        active.add(new Listener(method, clazz));
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
     * @return <tt>true</tt> if the {@link Event#isCanceled()}
     */
    public boolean dispatch(Event event)
    {
        Set<Listener> active = listeners.get(event.getClass());
        for (Listener listener : active)
        {
            listener.invoke(event);
        }

        return event.isCanceled();
    }
}
