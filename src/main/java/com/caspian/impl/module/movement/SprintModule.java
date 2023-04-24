package com.caspian.impl.module.movement;

import com.caspian.api.config.setting.EnumConfig;
import com.caspian.api.event.EventStage;
import com.caspian.api.event.listener.EventListener;
import com.caspian.api.module.ToggleModule;
import com.caspian.api.module.ModuleCategory;
import com.caspian.impl.event.TickEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SprintModule extends ToggleModule
{
    public final EnumConfig<SprintMode> modeConfig = new EnumConfig<>("Mode",
            "Sprinting mode. Rage allows for multi-directional sprinting.",
            SprintMode.LEGIT, SprintMode.values());
    /**
     *
     */
    public SprintModule()
    {
        super("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT);
    }

    /**
     *
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.POST)
        {

        }
    }

    public enum SprintMode
    {
        LEGIT,
        RAGE
    }
}
