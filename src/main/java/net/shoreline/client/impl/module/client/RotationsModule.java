package net.shoreline.client.impl.module.client;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.module.ConcurrentModule;
import net.shoreline.client.api.module.ModuleCategory;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class RotationsModule extends ConcurrentModule
{
    //
    Config<Float> preserveTicksConfig = new NumberConfig<>("PreserveTicks",
            "Time to preserve rotations after reaching the target rotations",
            0.0f, 20.0f, 20.0f);
    Config<Boolean> movementFixConfig = new BooleanConfig("MovementFix",
            "Fixes movement on NCP when rotating", false);

    /**
     *
     */
    public RotationsModule()
    {
        super("Rotations", "Manages client rotations",
                ModuleCategory.CLIENT);
    }

    /**
     *
     *
     * @return
     */
    public float getPreserveTicks()
    {
        return preserveTicksConfig.getValue();
    }
}
