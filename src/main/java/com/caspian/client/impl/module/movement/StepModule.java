package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class StepModule extends ToggleModule
{
    //
    Config<StepMode> modeConfig = new EnumConfig<>("Mode", "Step mode",
            StepMode.NORMAL, StepMode.values());
    Config<Float> heightConfig = new NumberConfig<>("Height", "The maximum " +
            "height for stepping up blocks", 1.0f, 2.5f, 10.0f);
    Config<Boolean> useTimerConfig = new BooleanConfig("UseTimer", "Slows " +
            "down packets by applying timer when stepping", true);
    Config<Boolean> strictConfig = new BooleanConfig("Strict", "Confirms the " +
            "step height for NCP servers", false, () -> heightConfig.getValue() <= 2.5f);
    Config<Boolean> entityStepConfig = new BooleanConfig("EntityStep",
            "Allows entities to step up blocks", false);

    /**
     *
     */
    public StepModule()
    {
        super("Step", "Allows the player to step up blocks",
                ModuleCategory.MOVEMENT);
    }

    public enum StepMode
    {
        VANILLA,
        NORMAL
    }
}
