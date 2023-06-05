package com.caspian.client.impl.module.movement;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SpeedModule extends ToggleModule
{
    /**
     *
     */
    public SpeedModule()
    {
        super("Speed", "Move faster", ModuleCategory.MOVEMENT);
    }

    private enum Speed
    {
        STRAFE,
        STRAFE_STRICT
    }
}
