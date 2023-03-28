package com.momentum.impl.handler;

import com.momentum.api.module.Module;
import com.momentum.api.registry.Registry;
import com.momentum.impl.module.ClickGuiModule;

import java.util.Collection;

/**
 * @author linus
 * @since 03/27/2023
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
        // register value set
        return register.values();
    }
}
