package com.caspian.api.module;

import com.caspian.impl.module.client.ClickGuiModule;
import com.caspian.impl.module.movement.SprintModule;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ModuleManager
{
    //
    private final ConcurrentMap<String, Module> modules =
            new ConcurrentHashMap<>();

    /**
     * Initializes the module register.
     */
    public ModuleManager()
    {
        // MAINTAIN ALPHABETICAL ORDER
        register(
                // Client
                new ClickGuiModule(),

                // Combat

                // Exploit

                // Movement
                new SprintModule()

                // Render

                // World
        );
    }

    /**
     *
     *
     * @param modules
     *
     * @see #register(Module)
     */
    private void register(Module... modules)
    {
        for (Module module : modules)
        {
            register(module);
        }
    }

    /**
     *
     *
     * @param module
     */
    private void register(Module module)
    {
        modules.put(module.getRef(), module);
    }

    /**
     *
     *
     * @param ref
     * @return
     */
    public Module getModule(String ref)
    {
        return modules.get(ref);
    }

    /**
     *
     *
     * @return
     */
    public Collection<Module> getModules()
    {
        return modules.values();
    }
}
