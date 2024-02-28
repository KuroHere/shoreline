package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.mixin.accessor.AccessorGameOptions;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.Timer;
import net.minecraft.client.render.entity.PlayerModelPart;

import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SkinBlinkModule extends ToggleModule
{
    //
    Config<Float> speedConfig = new NumberConfig<>("Speed", "The speed to " +
            "toggle the player model parts", 0.0f, 0.1f, 20.0f);
    Config<Boolean> randomConfig = new BooleanConfig("Random", "Randomizes " +
            "the toggling of each skin model part", false);
    //
    private final Timer blinkTimer = new CacheTimer();
    // The game option parts
    private Set<PlayerModelPart> enabledPlayerModelParts;

    /**
     *
     */
    public SkinBlinkModule()
    {
        super("SkinBlink", "Toggles the skin model rendering",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        enabledPlayerModelParts = ((AccessorGameOptions) mc.options).getPlayerModelParts();
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        for (PlayerModelPart modelPart : PlayerModelPart.values())
        {
            mc.options.togglePlayerModelPart(modelPart, enabledPlayerModelParts.contains(modelPart));
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.POST &&
                blinkTimer.passed(speedConfig.getValue() * 1000.0f))
        {
            final Set<PlayerModelPart> currentModelParts =
                    ((AccessorGameOptions) mc.options).getPlayerModelParts();;
            for (PlayerModelPart modelPart : PlayerModelPart.values())
            {
                mc.options.togglePlayerModelPart(modelPart, randomConfig.getValue() ?
                        Math.random() < 0.5 : !currentModelParts.contains(modelPart));
            }
            blinkTimer.reset();
        }
    }
}
