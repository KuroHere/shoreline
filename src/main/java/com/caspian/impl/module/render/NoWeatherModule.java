package com.caspian.impl.module.render;

import com.caspian.api.config.Config;
import com.caspian.api.config.setting.EnumConfig;
import com.caspian.api.event.listener.EventListener;
import com.caspian.api.module.ModuleCategory;
import com.caspian.api.module.ToggleModule;
import com.caspian.impl.event.TickEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NoWeatherModule extends ToggleModule
{
    //
    Config<Weather> weatherConfig = new EnumConfig<>("Weather", "World " +
            "weather", Weather.CLEAR, Weather.values());

    /**
     *
     */
    public NoWeatherModule(String name, String desc, ModuleCategory category)
    {
        super(name, desc, category);
    }

    // WHY THE FUCK DOES THIS NEED A HACK

    public enum Weather
    {
        CLEAR,
        RAIN,
        THUNDER,
        ASH
    }
}
