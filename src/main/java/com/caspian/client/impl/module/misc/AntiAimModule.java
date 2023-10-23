package com.caspian.client.impl.module.misc;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiAimModule extends ToggleModule
{
    /**
     *
     */
    public AntiAimModule()
    {
        super("AntiAim", "Makes it harder to accurately aim at the player",
                ModuleCategory.MISCELLANEOUS);
    }
}
