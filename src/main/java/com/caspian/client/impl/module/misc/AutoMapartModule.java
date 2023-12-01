package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoMapartModule extends ToggleModule
{
    //
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range to " +
            "place maps around the player", 0.1f, 6.0f, 10.0f);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotates " +
            "before placing maps", false);

    /**
     *
     */
    public AutoMapartModule()
    {
        super("AutoMapart", "Automatically places maparts on walls",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {

        }
    }
}
