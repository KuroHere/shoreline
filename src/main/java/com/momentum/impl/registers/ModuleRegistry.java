package com.momentum.impl.registers;

import com.momentum.api.module.Module;
import com.momentum.api.registry.Registry;
import com.momentum.impl.modules.client.clickgui.ClickGuiModule;
import com.momentum.impl.modules.client.color.ColorModule;
import com.momentum.impl.modules.client.hud.HudModule;
import com.momentum.impl.modules.miscellaneous.timer.TimerModule;
import com.momentum.impl.modules.movement.noslow.NoSlowModule;
import com.momentum.impl.modules.movement.speed.SpeedModule;
import com.momentum.impl.modules.movement.sprint.SprintModule;
import com.momentum.impl.modules.movement.velocity.VelocityModule;
import com.momentum.impl.modules.render.norender.NoRenderModule;

import java.util.Collection;

/**
 * Manages all client modules
 *
 * @author linus
 * @since 01/09/2023
 */
public class ModuleRegistry extends Registry<Module> {

    /**
     * Initializes module instances
     */
    public ModuleRegistry() {

        // initialize modules
        register(
                // COMBAT

                // EXPLOIT

                // MISCELLANEOUS
                new TimerModule(),

                // MOVEMENT
                new NoSlowModule(),
                new SpeedModule(),
                new SprintModule(),
                new VelocityModule(),

                // RENDER
                new NoRenderModule(),

                // WORLD

                // CLIENT
                new ClickGuiModule(),
                new ColorModule(),
                new HudModule()
        );
    }

    /**
     * Gets all client modules as a list
     *
     * @return All client modules as a list
     */
    public Collection<Module> getModules() {

        // list of modules
        return register.values();
    }
}
