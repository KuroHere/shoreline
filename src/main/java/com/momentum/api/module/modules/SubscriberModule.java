package com.momentum.api.module.modules;

import com.momentum.api.event.listener.Listener;
import com.momentum.api.module.Module;
import com.momentum.api.module.ModuleCategory;
import com.momentum.api.module.property.ISubscriber;
import com.momentum.api.module.exceptions.IncompatibleInterfaceException;
import com.momentum.api.registry.ILabeled;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Subscriber implementation in {@link Module}. Allows registering
 * {@link Listener} to a egistry backed by a {@link HashMap}.
 * 
 * @author linus
 * @since 03/22/2023
 */
public class SubscriberModule extends Module implements ISubscriber
{
    // listener register
    // underlying hash map with String keys
    private final Map<String, Listener> listenerRegister = new HashMap<>();

    /**
     * Default module constructor. Name must be unique for all modules.
     *
     * @param name     The module name
     * @param desc     The module description
     * @param category The module category
     * @throws IncompatibleInterfaceException if Module implements incompatible
     * interfaces
     */
    public SubscriberModule(String name, String desc, ModuleCategory category)
    {
        super(name, desc, category);
    }



    /**
     * Gets list of all active {@link Listener} listeners
     *
     * @return The list of all active {@link Listener} listeners
     */
    @Override
    public Collection<Listener> getListeners()
    {
        // register values
        return listenerRegister.values();
    }
}
