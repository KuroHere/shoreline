package com.momentum.api.event.listener;

import com.momentum.api.event.Event;
import com.momentum.api.event.listener.IListener;
import com.momentum.api.registry.ILabeled;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Listener implementation for {@link Event} event. Invokes when event
 * occurs.
 *
 * @author linus
 * @since 03/20/2023
 * @param <E> The event type
 */
public abstract class Listener<E extends Event>
        implements IListener<E>, ILabeled
{
    // The listening event class. This class is used to identify the event
    // that is associated with the current listener.
    private Class<E> event;

    /**
     * Initializes the listening event class
     */
    protected Listener()
    {
        // superclass
        Type superclass = getClass().getGenericSuperclass();

        // check if superclass contains a parameterized type
        if (superclass instanceof ParameterizedType)
        {
            // check parameter types
            for (Type t : ((ParameterizedType) superclass)
                    .getActualTypeArguments())
            {
                // check if parameter type is a class
                if (t instanceof Class)
                {
                    // express parameter type as Class obj
                    Class<E> event = (Class<E>) t;

                    // check if parameter type is Event subclass
                    if (Event.class.isAssignableFrom(event))
                    {
                        // initialize listener type
                        this.event = event;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Gets the listening event {@link Event} class
     *
     * @return The listening event {@link Event} class
     */
    @Override
    public Class<E> getEventClass()
    {
        return event;
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String getLabel()
    {
        return event.getCanonicalName() + "_listener";
    }

    /**
     * Calls the listener for the {@link Event}
     * event
     *
     * @param event The event
     */
    @Override
    public abstract void invoke(E event);
}
