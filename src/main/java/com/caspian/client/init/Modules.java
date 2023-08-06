package com.caspian.client.init;

import com.caspian.client.api.module.Module;
import com.caspian.client.api.manager.ModuleManager;
import com.caspian.client.impl.module.client.ClickGuiModule;
import com.caspian.client.impl.module.client.ColorsModule;
import com.caspian.client.impl.module.client.HudModule;
import com.caspian.client.impl.module.client.RotationsModule;
import com.caspian.client.impl.module.combat.AuraModule;
import com.caspian.client.impl.module.combat.AutoCrystalModule;
import com.caspian.client.impl.module.combat.AutoTotemModule;
import com.caspian.client.impl.module.combat.CriticalsModule;
import com.caspian.client.impl.module.exploit.SwingModule;
import com.caspian.client.impl.module.misc.TimerModule;
import com.caspian.client.impl.module.movement.SpeedModule;
import com.caspian.client.impl.module.movement.VelocityModule;
import com.caspian.client.impl.module.exploit.AntiHungerModule;
import com.caspian.client.impl.module.movement.NoSlowModule;
import com.caspian.client.impl.module.movement.SprintModule;
import com.caspian.client.impl.module.render.*;
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
    public static final AuraModule AURA;
    public static final AutoCrystalModule AUTO_CRYSTAL;
    public static final AutoTotemModule AUTO_TOTEM;
    public static final CriticalsModule CRITICALS;
    public static final VelocityModule VELOCITY;
    public static final AntiHungerModule ANTI_HUNGER;
    public static final SwingModule SWING;
    public static final TimerModule TIMER;
    public static final NoSlowModule NO_SLOW;
    public static final SpeedModule SPEED;
    public static final SprintModule SPRINT;
    public static final BlockHighlightModule BLOCK_HIGHLIGHT;
    public static final FullbrightModule FULLBRIGHT;
    // public static final NoWeatherModule NO_WEATHER;
    // public static final ViewClipModule VIEW_CLIP;
    // public static final ViewModelModule VIEW_MODEL;
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
                    "clickgui-module");
            COLORS = (ColorsModule) getRegisteredModule("colors-module");
            HUD = (HudModule) getRegisteredModule("hud-module");
            ROTATIONS = (RotationsModule) getRegisteredModule(
                    "rotations-module");
            AURA = (AuraModule) getRegisteredModule("aura-module");
            AUTO_CRYSTAL = (AutoCrystalModule) getRegisteredModule(
                    "autocrystal-module");
            AUTO_TOTEM = (AutoTotemModule) getRegisteredModule(
                    "autototem-module");
            CRITICALS = (CriticalsModule) getRegisteredModule("criticals-module");
            ANTI_HUNGER = (AntiHungerModule) getRegisteredModule(
                    "antihunger-module");
            SWING = (SwingModule) getRegisteredModule("swing-module");
            TIMER = (TimerModule) getRegisteredModule("timer-module");
            NO_SLOW = (NoSlowModule) getRegisteredModule("noslow-module");
            SPEED = (SpeedModule) getRegisteredModule("speed-module");
            SPRINT = (SprintModule) getRegisteredModule("sprint-module");
            VELOCITY = (VelocityModule) getRegisteredModule("velocity");
            BLOCK_HIGHLIGHT = (BlockHighlightModule) getRegisteredModule(
                    "blockhighlight-module");
            FULLBRIGHT = (FullbrightModule) getRegisteredModule(
                    "fullbright-module");
            // NO_WEATHER = (NoWeatherModule) getRegisteredModule(
            //        "noweather-module");
            // VIEW_CLIP = (ViewClipModule) getRegisteredModule(
            //        "viewclip-module");
            // VIEW_MODEL = (ViewModelModule) getRegisteredModule(
            //        "viewmodel-module");
            FAST_PLACE = (FastPlaceModule) getRegisteredModule(
                    "fastplace-module");
            SPEEDMINE = (SpeedmineModule) getRegisteredModule(
                    "speedmine-module");
            // reflect configuration properties for each cached module
            for (Module module : CACHE)
            {
                if (module == null)
                {
                    continue;
                }
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
