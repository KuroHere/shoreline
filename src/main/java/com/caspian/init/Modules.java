package com.caspian.init;

import com.caspian.api.module.Module;
import com.caspian.api.module.ModuleManager;
import com.caspian.impl.module.client.ClickGuiModule;
import com.caspian.impl.module.movement.SprintModule;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Module
 */
public class Modules
{
    // The module initialization cache. This prevents modules from being
    // initialized more than once.
    private static final Set<Module> CACHE;

    // module instances.
    public static final ClickGuiModule CLICKGUI;
    public static final SprintModule SPRINT;

    /**
     * Returns the registered {@link Module} with the param name in the
     * {@link ModuleManager}. The same module
     * cannot be retrieved more than once using this method.
     *
     * @param id The module name
     * @return The retrieved module
     * @throws IllegalStateException If the module was not registered
     *
     * @see ModuleManager
     */
    private static Module getRegisteredModule(String id)
    {
        Module registered = Managers.MODULE.getModule(id);
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
        if (Managers.isInitialized())
        {
            CACHE = new HashSet<>();
            CLICKGUI = (ClickGuiModule) getRegisteredModule(
                    "clickgui_module");
            SPRINT = (SprintModule) getRegisteredModule("sprint_module");
            CACHE.clear();
        }

        else
        {
            throw new RuntimeException("Accessed modules before managers " +
                    "finished initializing!");
        }
    }
}
