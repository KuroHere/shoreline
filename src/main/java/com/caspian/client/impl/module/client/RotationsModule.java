package com.caspian.client.impl.module.client;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.module.ConcurrentModule;
import com.caspian.client.api.module.ModuleCategory;

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
     * @return
     */
    public float getPreserveTicks()
    {
        return preserveTicksConfig.getValue();
    }
}
