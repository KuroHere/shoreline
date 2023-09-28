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
public class ElytraFlyModule extends ToggleModule
{
    //
    Config<FlyMode> modeConfig = new EnumConfig<>("Mode", "The mode for " +
            "elytra flight", FlyMode.CONTROL, FlyMode.values());
    Config<Float> speedConfig = new NumberConfig<>("Speed", "The horizontal " +
            "flight speed", 0.1f, 2.5f, 10.0f);
    Config<Float> vspeedConfig = new NumberConfig<>("VerticalSpeed", "The " +
            "vertical flight speed", 0.1f, 1.0f, 5.0f);
    Config<Boolean> fireworkConfig = new BooleanConfig("Fireworks", "Uses " +
            "fireworks when flying", false, () -> modeConfig.getValue() != FlyMode.PACKET);

    /**
     *
     */
    public ElytraFlyModule()
    {
        super("ElytraFly", "Allows you to fly freely using an elytra",
                ModuleCategory.MOVEMENT);
    }

    public enum FlyMode
    {
        CONTROL,
        CONTROL_STRICT,
        ACCEL,
        BOOST,
        FACTORIZE,
        PACKET,
        BOUNCE
    }
}
