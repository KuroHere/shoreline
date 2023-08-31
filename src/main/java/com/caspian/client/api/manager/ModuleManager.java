package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.module.client.ClickGuiModule;
import com.caspian.client.impl.module.client.ColorsModule;
import com.caspian.client.impl.module.client.HudModule;
import com.caspian.client.impl.module.client.RotationsModule;
import com.caspian.client.impl.module.combat.AuraModule;
import com.caspian.client.impl.module.combat.AutoCrystalModule;
import com.caspian.client.impl.module.combat.AutoTotemModule;
import com.caspian.client.impl.module.combat.CriticalsModule;
import com.caspian.client.impl.module.exploit.AntiHungerModule;
import com.caspian.client.impl.module.exploit.SwingModule;
import com.caspian.client.impl.module.misc.FakePlayerModule;
import com.caspian.client.impl.module.misc.TimerModule;
import com.caspian.client.impl.module.movement.*;
import com.caspian.client.impl.module.render.*;
import com.caspian.client.impl.module.world.FastPlaceModule;
import com.caspian.client.impl.module.world.SpeedmineModule;
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
    // The client module register. Keeps a list of modules and their ids for
    // easy retrieval by id.
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
                new ColorsModule(),
                new HudModule(),
                new RotationsModule(),
                // Combat
                new AuraModule(),
                new AutoCrystalModule(),
                new AutoTotemModule(),
                new CriticalsModule(),
                // Exploit
                new AntiHungerModule(),
                new SwingModule(),
                // Misc
                new FakePlayerModule(),
                new TimerModule(),
                // Movement
                new FastFallModule(),
                new NoSlowModule(),
                new SpeedModule(),
                new SprintModule(),
                new VelocityModule(),
                // Render
                new BlockHighlightModule(),
                new FullbrightModule(),
                new NametagsModule(),
                new NoRenderModule(),
                new NoWeatherModule(),
                // World
                new FastPlaceModule(),
                new SpeedmineModule()
        );
        Caspian.info("Registered {} modules!", modules.size());
    }

    /**
     *
     */
    public void postInit()
    {
        // TODO
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
