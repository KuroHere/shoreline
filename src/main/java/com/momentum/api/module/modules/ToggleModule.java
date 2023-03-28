package com.momentum.api.module.modules;

import com.momentum.Momentum;
import com.momentum.api.config.Configuration;
import com.momentum.api.config.configs.BooleanConfig;
import com.momentum.api.event.Listener;
import com.momentum.api.module.ModuleCategory;
import com.momentum.api.module.exceptions.IncompatibleInterfaceException;
import com.momentum.api.module.property.IToggleable;

/**
 * Module implementation with an enabled state that can be toggled on and off.
 *
 * @author linus
 * @since 03/21/2023
 */
public class ToggleModule extends SubscriberModule implements IToggleable
{
    // enabled state
    // default set to false
    @Configuration("module_enabled")
    final BooleanConfig enabled = new BooleanConfig("Enabled",
            "Enabled state. Global in toggleable modules", false);

    /**
     * Default module constructor. Name must be unique for all modules.
     *
     * @param name     The module name
     * @param desc     The module description
     * @param category The module category
     * @throws IncompatibleInterfaceException if module implements incompatible
     * interfaces
     */
    public ToggleModule(String name, String desc, ModuleCategory category)
    {
        super(name, desc, category);
    }

    /**
     * Toggles the enable state
     */
    @Override
    public void toggle()
    {
        // check enabled state
        if (isEnabled())
        {
            disable();
        }

        // not enabled
        else
        {
            enable();
        }

        // event
        onToggle();
    }

    /**
     * Enables the feature
     */
    @Override
    public void enable()
    {
        // enable
        enabled.setValue(true);
        onEnable();
    }

    /**
     * Disables the object
     */
    @Override
    public void disable()
    {
        // disable
        enabled.setValue(false);
        onDisable();
    }

    /**
     * Called when the object is toggled
     */
    @Override
    public void onToggle()
    {
        // impl in module
    }

    /**
     * Called when the object is enabled
     */
    @Override
    public void onEnable()
    {
        // subscribe all active listeners
        for (Listener l : getListeners())
        {
            Momentum.EVENT_HANDLER.subscribe(l);
        }
    }

    /**
     * Called when the object is disabled
     */
    @Override
    public void onDisable()
    {
        // unsubscribe all active listeners
        for (Listener l : getListeners())
        {
            Momentum.EVENT_HANDLER.unsubscribe(l);
        }
    }

    /**
     * Returns whether the enabled state is true
     *
     * @return The enabled state = true
     */
    @Override
    public boolean isEnabled()
    {
        // enabled config value
        return enabled.getValue();
    }
}
