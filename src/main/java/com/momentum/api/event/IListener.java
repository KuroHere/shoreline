package com.momentum.api.event;

/**
 * Listener for {@link Event} event. Invokes when event occurs.
 *
 * @author linus
 * @since 03/20/2023
 * @param <E> Event type to listen for
 */
public interface IListener<E extends Event> extends IInvoker<E>
{
    /**
     * Calls the listener for the {@link Event} event
     *
     * @param event The event
     */
    void invoke(E event);

    /**
     * Calls the listener for the given {@link Event} event
     *
     * @param events The events
     */
    @Override
    default void invoke(E[] events)
    {
        // no impl
    }

    /**
     * Gets the listening event {@link Event} class
     *
     * @return The listening event {@link Event} class
     */
    Class<E> getEventClass();
}
