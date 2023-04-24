package com.caspian.api.event.listener;

import com.caspian.Caspian;
import com.caspian.api.Invoker;
import com.caspian.api.event.Event;
import com.caspian.api.event.handler.EventHandler;

import java.lang.invoke.*;
import java.lang.reflect.Method;

/**
 * {@link Event} Listener that creates an {@link Invoker} and runs {@link #invoke(Event)}
 * when the event is dispatched by the {@link EventHandler}.
 *
 * <p>The invoker is created using {@link LambdaMetafactory} which is nearly
 * as fast as direct access; This method of invocation allows for blazing
 * fast event handling. Event Listeners can be created with the
 * {@link EventListener} annotation, for example:<p/>
 * <pre>{@code
 * @EventListener
 * public void onEvent(Event e)
 * {
 *     // your code ...
 * }
 * }</pre>
 *
 * @author linus
 * @since 1.0
 *
 * @see Event
 * @see EventHandler
 * @see EventListener
 */
public class Listener implements Invoker<Event>
{
    // The EventListener method which contains the code to invoke when the
    // listener is invoked.
    private final Method method;

    // The class that contains the EventListener. This class must be
    // subscribed to the EventHandler in order for this Listener to be invoked.
    private final Class<?> subscriber;

    // The Listener invoker created by the LambdaMetaFactory which invokes the
    // code from the Listener method.
    private Invoker invoker;

    /**
     *
     *
     * @param method
     * @param subscriber
     */
    public Listener(Method method, Class<?> subscriber)
    {
        this.method = method;
        this.subscriber = subscriber;

        // creates a method handler using LambdaMetaFactory
        try
        {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            CallSite callSite = LambdaMetafactory.metafactory(lookup,
                    "invoke", MethodType.methodType(Invoker.class)
                            .appendParameterTypes(subscriber),
                    MethodType.methodType(Void.TYPE, Event.class),
                    lookup.unreflect(method),
                    MethodType.methodType(Void.TYPE, method.getParameterTypes()[0]));
            invoker = (Invoker) callSite.getTarget().invoke(subscriber);
        }

        catch (Throwable e)
        {
            Caspian.error("Failed to build invoker for " + method.getName());
            e.printStackTrace();
        }
    }

    /**
     *
     *
     * @param event
     *
     * @see Invoker#invoke(Object)
     */
    @Override
    public void invoke(Event event)
    {
        invoker.invoke(event);
    }

    /**
     *
     *
     * @return
     */
    public Method getMethod()
    {
        return method;
    }

    /**
     *
     *
     * @return
     */
    public Class<?> getSubscriber()
    {
        return subscriber;
    }
}
