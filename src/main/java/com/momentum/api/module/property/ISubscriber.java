package com.momentum.api.module.property;

import com.momentum.api.event.listener.Listener;
import com.momentum.api.module.modules.SubscriberModule;

import java.util.Collection;

/**
 * Event listening registry implementation. Interface enabling {@link com.momentum.api.event.Event}
 * listening through {@link Listener}.
 *
 * @author linus
 * @since 03/22/2023
 *
 * @see SubscriberModule
 */
public interface ISubscriber
{
    /**
     * Gets list of all active {@link Listener} listeners
     *
     * @return The list of all active {@link Listener} listeners
     */
     Collection<Listener> getListeners();
}
