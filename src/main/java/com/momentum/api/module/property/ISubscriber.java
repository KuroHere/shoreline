package com.momentum.api.module.property;

import com.momentum.api.event.Listener;
import com.momentum.api.registry.ILabeled;

import java.util.Collection;

/**
 * Event listening registry implementation. Interface enabling {@link com.momentum.api.event.Event}
 * listening through {@link Listener}.
 *
 * @author linus
 * @since 03/22/2023
 *
 * @see com.momentum.api.module.modules.SubscriberModule
 */
public interface ISubscriber {

    /**
     * Registers the given data to a register, which can later be subscribed to an
     * event bus
     *
     * @param data The data
     * @return The registered data
     * @throws NullPointerException if data is <tt>null</tt>
     */
    <L, C extends Listener> C register(C data);

    /**
     * Unregisters the given data to a register, which removes its mapping from the
     * register. Unregistering data also frees its {@link ILabeled} label. Unsubscribes
     * from the event bus.
     *
     * @param data The data
     * @return The unregistered data
     * @throws NullPointerException if data is <tt>null</tt>
     */
    Listener<?> unregister(Listener data);

    /**
     * Gets list of all active {@link Listener} listeners
     *
     * @return The list of all active {@link Listener} listeners
     */
     Collection<Listener> getListeners();
}
