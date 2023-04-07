package com.momentum.impl.handler;

import com.momentum.api.module.Module;
import com.momentum.api.registry.Registry;
import com.momentum.impl.module.client.ClickGuiModule;

import java.util.Collection;

/**
 * Handles registration of {@link Module}
 *
 * @author linus
 * @since 1.0
 *
 * @see Module
 */
public class ModuleHandler extends Registry<Module>
{
    /**
     * Initializes the module registry
     */
    public ModuleHandler()
    {
        // MAINTAIN ORDER
        register(
                // client modules
                new ClickGuiModule()
        );
    }

    /**
     * Gets the set of modules in the register
     *
     * @return The set of module
     */
    public Collection<Module> getModules()
    {
        return register.values();
    }
}
