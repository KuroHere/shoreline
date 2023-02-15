package com.momentum.impl.init;

import com.momentum.Momentum;
import com.momentum.api.module.Module;
import com.momentum.impl.modules.client.clickgui.ClickGuiModule;
import com.momentum.impl.modules.client.color.ColorModule;
import com.momentum.impl.modules.client.hud.HudModule;
import com.momentum.impl.modules.miscellaneous.timer.TimerModule;
import com.momentum.impl.modules.movement.noslow.NoSlowModule;
import com.momentum.impl.modules.movement.speed.SpeedModule;
import com.momentum.impl.modules.movement.sprint.SprintModule;
import com.momentum.impl.modules.movement.velocity.VelocityModule;
import com.momentum.impl.modules.render.norender.NoRenderModule;

/**
 * @author linus
 * @since 02/11/2023
 */
public class Modules {

    // MAINTAIN ORDER
    // module instances
    public static final ColorModule COLOR_MODULE;
    public static final ClickGuiModule CLICKGUI_MODULE;
    public static final HudModule HUD_MODULE;
    public static final TimerModule TIMER_MODULE;
    public static final NoSlowModule NOSLOW_MODULE;
    public static final SpeedModule SPEED_MODULE;
    public static final SprintModule SPRINT_MODULE;
    public static final VelocityModule VELOCITY_MODULE;
    public static final NoRenderModule NORENDER_MODULE;

    /**
     * Gets the registered module
     *
     * @param label The module label
     * @return The registered module
     */
    private static Module getRegisteredModule(String label) {

        // module from registry
        return Momentum.MODULE_REGISTRY.lookup(label);
    }

    static {

        // COMBAT

        // EXPLOIT

        // MISCELLANEOUS
        TIMER_MODULE = (TimerModule) getRegisteredModule("timer_module");

        // MOVEMENT
        NOSLOW_MODULE = (NoSlowModule) getRegisteredModule("noslow_module");
        SPEED_MODULE = (SpeedModule) getRegisteredModule("speed_module");
        SPRINT_MODULE = (SprintModule) getRegisteredModule("sprint_module");
        VELOCITY_MODULE = (VelocityModule) getRegisteredModule("velocity_module");

        // RENDER
        NORENDER_MODULE = (NoRenderModule) getRegisteredModule("norender_module");

        // WORLD

        // CLIENT
        CLICKGUI_MODULE = (ClickGuiModule) getRegisteredModule("clickgui_module");
        COLOR_MODULE = (ColorModule) getRegisteredModule("color_module");
        HUD_MODULE = (HudModule) getRegisteredModule("hud_module");
    }
}
