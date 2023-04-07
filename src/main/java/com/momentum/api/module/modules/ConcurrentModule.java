package com.momentum.api.module.modules;

import com.momentum.Momentum;
import com.momentum.api.event.listener.Listener;
import com.momentum.api.module.ModuleCategory;
import com.momentum.api.module.property.IConcurrent;
import com.momentum.api.module.exceptions.IncompatibleInterfaceException;

/**
 * Module that runs concurrently to all other modules. Unlike
 * {@link ToggleModule}, ConcurrentModule cannot be toggled on and off.
 *
 * @author linus
 * @since 03/21/2023
 */
public class ConcurrentModule extends SubscriberModule implements IConcurrent
{
    /**
     * Default module constructor. Name must be unique for all modules.
     *
     * @param name     The module name
     * @param desc     The module description
     * @param category The module category
     * @throws IncompatibleInterfaceException if Module implements incompatible
     * interfaces
     */
    public ConcurrentModule(String name, String desc, ModuleCategory category)
    {
        super(name, desc, category);

        // subscribe all listeners
        for (Listener<?> l : getListeners())
        {
            Momentum.EVENT_HANDLER.subscribe(l);
        }
    }
}
