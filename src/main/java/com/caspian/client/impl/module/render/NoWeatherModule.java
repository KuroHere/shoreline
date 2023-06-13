package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

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
    public NoWeatherModule()
    {
        super("NoWeather", "Prevents weather rendering", ModuleCategory.RENDER);
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
