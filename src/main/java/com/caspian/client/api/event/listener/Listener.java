package com.caspian.client.api.event.listener;

import com.caspian.client.Caspian;
import com.caspian.client.api.Invoker;
import com.caspian.client.api.event.Event;
import com.caspian.client.api.event.handler.EventHandler;

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
public class Listener
{
    // The EventListener method which contains the code to invoke when the
    // listener is invoked.
    private final Method method;
    // Static instance of method "privateLookupIn"
    private static Method privateLookup;
    // The class that contains the EventListener. This class must be
    // subscribed to the EventHandler in order for this Listener to be invoked.
    private final Class<?> subscriber;
    // The Listener invoker created by the LambdaMetaFactory which invokes the
    // code from the Listener method.
    private Invoker<Event> invoker;

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
        // lambda at runtime to call the method
        try
        {
            MethodHandles.Lookup lookup =
                    (MethodHandles.Lookup) privateLookup.invoke(null,
                    subscriber, MethodHandles.lookup());
            MethodType type = MethodType.methodType(void.class,
                    method.getParameters()[0].getType());
            MethodHandle handle = lookup.findVirtual(subscriber,
                    method.getName(), type);
            MethodType invokeType = MethodType.methodType(Invoker.class,
                    subscriber);
            CallSite factory = LambdaMetafactory.metafactory(lookup,
                    "accept", invokeType, MethodType.methodType(void.class,
                            Object.class), handle, type);
            invoker = (Invoker<Event>) factory.getTarget().invoke(subscriber);
        }
        catch (Throwable e)
        {
            Caspian.error("Failed to build invoker for %s", method.getName());
            e.printStackTrace();
        }
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

    /**
     *
     *
     * @param event
     *
     * @see Invoker#invoke(Object)
     */
    public void invoke(Event event)
    {
        invoker.invoke(event);
    }

    static
    {
        try
        {
            privateLookup = MethodHandles.class.getDeclaredMethod(
                    "privateLookupIn", Class.class, MethodHandles.Lookup.class);
        }
        catch (NoSuchMethodException e)
        {
            Caspian.error("Could not find method privateLookupIn!");
            e.printStackTrace();
        }
    }
}
