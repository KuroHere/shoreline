package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.module.client.ClickGuiModule;
import com.caspian.client.impl.module.movement.SprintModule;
import com.caspian.client.init.Managers;

import java.util.*;

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
            Collections.synchronizedMap(new LinkedHashMap<>());

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
                // new AutoCrystalModule(),
                // Exploit
                // Movement
                new SprintModule()
                // Render
                // World
        );
        Caspian.info("Registered {} modules!", modules.size());
    }

    /**
     * Post initialization stage for all modules, used to initialize additional
     * info that may not have been initialized in {@link Caspian#init()}
     */
    public void postInit()
    {
        for (Module module : modules.values())
        {
            if (module instanceof ToggleModule toggle)
            {
                Managers.MACRO.register(toggle.getKeybinding());
            }
        }
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
    public List<Module> getModules()
    {
        return new ArrayList<>(modules.values());
    }
}
