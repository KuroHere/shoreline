package com.caspian.client.api.event.handler;

import com.caspian.client.api.event.Event;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Event
 */
public interface EventBus
{
    /**
     *
     * @param obj
     */
    void subscribe(Object obj);

    /**
     *
     * @param obj
     */
    void unsubscribe(Object obj);

    /**
     *
     * @param event
     * @return
     */
    boolean dispatch(Event event);
}
