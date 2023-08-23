package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.config.ConfigUpdateEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NoWeatherModule extends ToggleModule
{
    // WHY THE FUCK DOES THIS NEED A HACK
    //
    Config<Weather> weatherConfig = new EnumConfig<>("Weather", "The world " +
            "weather", Weather.CLEAR, Weather.values());
    Config<Integer> dayTimeConfig = new NumberConfig<>("Time", "The world " +
            "time of day", 0, 6000, 24000);
    //
    private Weather weather;

    /**
     *
     */
    public NoWeatherModule()
    {
        super("NoWeather", "Prevents weather rendering", ModuleCategory.RENDER);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        if (mc.world != null)
        {
            if (mc.world.isThundering())
            {
                weather = Weather.THUNDER;
            }
            else if (mc.world.isRaining())
            {
                weather = Weather.RAIN;
            }
            else
            {
                weather = Weather.CLEAR;
            }
            setWeather(weatherConfig.getValue());
            mc.world.setTimeOfDay(dayTimeConfig.getValue());
        }
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        if (mc.world != null && weather != null)
        {
            setWeather(weather);
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onConfigUpdate(ConfigUpdateEvent event)
    {
        if (mc.world != null && event.getStage() == EventStage.POST)
        {
            Config<?> config = event.getConfig();
            if (config == weatherConfig)
            {
                setWeather(weatherConfig.getValue());
            }
            else if (config == dayTimeConfig)
            {
                mc.world.setTimeOfDay(dayTimeConfig.getValue());
            }
        }
    }

    /**
     *
     * @param weather
     */
    private void setWeather(Weather weather)
    {
        switch (weather)
        {
            case CLEAR ->
            {
                mc.world.getLevelProperties().setRaining(false);
                mc.world.setRainGradient(0.0f);
                mc.world.setThunderGradient(0.0f);
            }
            case RAIN ->
            {
                mc.world.getLevelProperties().setRaining(true);
                mc.world.setRainGradient(1.0f);
                mc.world.setThunderGradient(0.0f);
            }
            case THUNDER ->
            {
                mc.world.getLevelProperties().setRaining(true);
                mc.world.setRainGradient(2.0f);
                mc.world.setThunderGradient(1.0f);
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (event.getPacket() instanceof GameStateChangeS2CPacket packet)
        {
            if (packet.getReason() == GameStateChangeS2CPacket.RAIN_STARTED
                    || packet.getReason() == GameStateChangeS2CPacket.RAIN_STOPPED
                    || packet.getReason() == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED
                    || packet.getReason() == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED)
            {
                event.cancel();
            }
        }
        else if (event.getPacket() instanceof WorldTimeUpdateS2CPacket)
        {
            event.cancel();
        }
    }

    public enum Weather
    {
        CLEAR,
        RAIN,
        THUNDER,
        ASH
    }
}
