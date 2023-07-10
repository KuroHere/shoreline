package com.caspian.client.init;

import com.caspian.client.api.module.Module;
import com.caspian.client.api.manager.ModuleManager;
import com.caspian.client.impl.module.client.ClickGuiModule;
import com.caspian.client.impl.module.client.ColorsModule;
import com.caspian.client.impl.module.client.HudModule;
import com.caspian.client.impl.module.client.RotationsModule;
import com.caspian.client.impl.module.combat.AutoCrystalModule;
import com.caspian.client.impl.module.combat.AutoTotemModule;
import com.caspian.client.impl.module.movement.VelocityModule;
import com.caspian.client.impl.module.exploit.AntiHungerModule;
import com.caspian.client.impl.module.movement.NoSlowModule;
import com.caspian.client.impl.module.movement.SprintModule;
import com.caspian.client.impl.module.render.FullbrightModule;
import com.caspian.client.impl.module.render.NoWeatherModule;
import com.caspian.client.impl.module.render.ViewClipModule;
import com.caspian.client.impl.module.render.ViewModelModule;
import com.caspian.client.impl.module.world.FastPlaceModule;
import com.caspian.client.impl.module.world.SpeedmineModule;

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
    // Module instances.
    public static final ClickGuiModule CLICK_GUI;
    public static final ColorsModule COLORS;
    public static final HudModule HUD;
    public static final RotationsModule ROTATIONS;
    public static final AutoCrystalModule AUTO_CRYSTAL;
    public static final AutoTotemModule AUTO_TOTEM;
    public static final VelocityModule VELOCITY;
    public static final AntiHungerModule ANTI_HUNGER;
    public static final NoSlowModule NO_SLOW;
    public static final SprintModule SPRINT;
    public static final FullbrightModule FULLBRIGHT;
    public static final NoWeatherModule NO_WEATHER;
    public static final ViewClipModule VIEW_CLIP;
    public static final ViewModelModule VIEW_MODEL;
    public static final FastPlaceModule FAST_PLACE;
    public static final SpeedmineModule SPEEDMINE;

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
            ROTATIONS = (RotationsModule) getRegisteredModule(
                    "rotations_module");
            AUTO_CRYSTAL = (AutoCrystalModule) getRegisteredModule(
                    "autocrystal_module");
            AUTO_TOTEM = (AutoTotemModule) getRegisteredModule(
                    "autototem_module");
            VELOCITY = (VelocityModule) getRegisteredModule("velocity");
            ANTI_HUNGER = (AntiHungerModule) getRegisteredModule(
                    "antihunger_module");
            NO_SLOW = (NoSlowModule) getRegisteredModule("noslow_module");
            SPRINT = (SprintModule) getRegisteredModule("sprint_module");
            FULLBRIGHT = (FullbrightModule) getRegisteredModule(
                    "fullbright_module");
            NO_WEATHER = (NoWeatherModule) getRegisteredModule(
                    "noweather_module");
            VIEW_CLIP = (ViewClipModule) getRegisteredModule(
                    "viewclip_module");
            VIEW_MODEL = (ViewModelModule) getRegisteredModule(
                    "viewmodel_module");
            FAST_PLACE = (FastPlaceModule) getRegisteredModule(
                    "fastplace_module");
            SPEEDMINE = (SpeedmineModule) getRegisteredModule(
                    "speedmine_module");
            // reflect configuration properties for each cached module
            for (Module module : CACHE)
            {
                module.reflectConfigs();
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
