package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
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
public class FastFallModule extends ToggleModule
{
    //
    Config<FallMode> fallModeConfig = new EnumConfig<>("Mode", "The mode for " +
            "falling down blocks", FallMode.STEP, FallMode.values());
    Config<Integer> shiftTicksConfig = new NumberConfig<>("ShiftTicks",
            "Number of ticks to shift ahead", 0, 3, 5,
            () -> fallModeConfig.getValue() == FallMode.SHIFT);

    /**
     *
     */
    public FastFallModule()
    {
        super("FastFall", "Falls down blocks faster", ModuleCategory.MOVEMENT);
    }

    public enum FallMode
    {
        STEP,
        SHIFT
    }
}
