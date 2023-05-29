package com.caspian.client.init;

import com.caspian.client.api.module.Module;
import com.caspian.client.api.manager.ModuleManager;
import com.caspian.client.impl.module.client.ClickGuiModule;
import com.caspian.client.impl.module.client.ColorsModule;
import com.caspian.client.impl.module.client.HudModule;
import com.caspian.client.impl.module.combat.AutoCrystalModule;
import com.caspian.client.impl.module.movement.SprintModule;

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
    public static final ClickGuiModule CLICK_GUI;
    public static final ColorsModule COLORS;
    public static final HudModule HUD;
    public static final AutoCrystalModule AUTO_CRYSTAL;
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
            CLICK_GUI = (ClickGuiModule) getRegisteredModule(
                    "clickgui_module");
            COLORS = (ColorsModule) getRegisteredModule("colors_module");
            HUD = (HudModule) getRegisteredModule("hud_module");
            AUTO_CRYSTAL = (AutoCrystalModule) getRegisteredModule(
                    "autocrystal_module");
            SPRINT = (SprintModule) getRegisteredModule("sprint_module");

            // reflect configuration properties for each cached module
            for (Module cachedModule : CACHE)
            {
                cachedModule.reflectConfigurations();
            }

            CACHE.clear();
        }

        else
        {
            throw new RuntimeException("Accessed modules before managers " +
                    "finished initializing!");
        }
    }
}
