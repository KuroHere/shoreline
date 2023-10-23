package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.module.Module;
import com.caspian.client.impl.module.client.ClickGuiModule;
import com.caspian.client.impl.module.client.ColorsModule;
import com.caspian.client.impl.module.client.HudModule;
import com.caspian.client.impl.module.client.RotationsModule;
import com.caspian.client.impl.module.combat.*;
import com.caspian.client.impl.module.exploit.*;
import com.caspian.client.impl.module.misc.*;
import com.caspian.client.impl.module.movement.*;
import com.caspian.client.impl.module.render.*;
import com.caspian.client.impl.module.world.AvoidModule;
import com.caspian.client.impl.module.world.FastPlaceModule;
import com.caspian.client.impl.module.world.SpeedmineModule;

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
                new AutoArmorModule(),
                new AutoBowReleaseModule(),
                new AutoCrystalModule(),
                new AutoTotemModule(),
                new CriticalsModule(),
                // Exploit
                new AntiHungerModule(),
                new FakeLatencyModule(),
                new FastProjectileModule(),
                new NoMineAnimationModule(),
                new PortalGodModeModule(),
                new ReachModule(),
                new SwingModule(),
                // Misc
                new AntiBookBanModule(),
                new AntiSpamModule(),
                new AntiVanishModule(),
                new AutoFishModule(),
                new AutoRespawnModule(),
                new FakePlayerModule(),
                new MiddleClickModule(),
                new NoSoundLagModule(),
                new SkinBlinkModule(),
                new TimerModule(),
                new TrueDurabilityModule(),
                new XCarryModule(),
                // Movement
                new AntiLevitationModule(),
                new EntityControlModule(),
                new FastFallModule(),
                new FlightModule(),
                new HighJumpModule(),
                new IceSpeedModule(),
                new LongJumpModule(),
                new NoFallModule(),
                new NoSlowModule(),
                new SpeedModule(),
                new SprintModule(),
                new StepModule(),
                new TickShiftModule(),
                new VelocityModule(),
                new YawModule(),
                // Render
                new BlockHighlightModule(),
                new ESPModule(),
                new ExtraTabModule(),
                new FullbrightModule(),
                new NametagsModule(),
                new NoBobModule(),
                new NoRenderModule(),
                new NoRotateModule(),
                new NoWeatherModule(),
                new ViewClipModule(),
                // World
                new AvoidModule(),
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
