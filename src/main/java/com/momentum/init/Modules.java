package com.momentum.init;

import com.momentum.api.module.Module;
import com.momentum.impl.handler.ModuleHandler;
import com.momentum.impl.module.ClickGuiModule;

import java.util.HashSet;
import java.util.Set;

/**
 * Initialization helper for {@link Module}. Contains all static instances of
 * the modules in {@link ModuleHandler#getModules()}
 *
 * @author linus
 * @since 03/27/2023
 */
public class Modules
{
    // initialization cache
    private static final Set<Module> CACHE;
    public static final ClickGuiModule CLICKGUI;

    /**
     * Returns the registered module from the id
     *
     * @param id The module id
     * @return The retrieved module
     */
    private static Module getRegisteredModule(String id)
    {
        // retrieved module
        final Module registered = Handlers.MODULE.retrieve(id);
        if (CACHE.add(registered))
        {
            return registered;
        }

        // already cached!!
        else
        {
            throw new IllegalStateException("Invalid module requested: " + id);
        }
    }

    static
    {
        // start init
        if (Bootstrap.isInitialized())
        {
            CACHE = new HashSet<>();
            CLICKGUI = (ClickGuiModule) getRegisteredModule("clickgui_module");
            CACHE.clear();
        }

        // boot strap not registered
        else
        {
            throw new RuntimeException("Accessed modules before Bootstrap " +
                    "finished initializing!");
        }
    }
}
