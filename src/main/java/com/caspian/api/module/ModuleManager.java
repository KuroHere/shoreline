package com.caspian.api.module;

import com.caspian.impl.module.client.ClickGuiModule;
import com.caspian.impl.module.movement.SprintModule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ModuleManager
{
    //
    private final Map<String, Module> modules =
            Collections.synchronizedMap(new HashMap<>());

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
        modules.put(module.getId(), module);
    }

    /**
     *
     *
     * @param id
     * @return
     */
    public Module getModule(String id)
    {
        return modules.get(id);
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
