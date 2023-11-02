package com.caspian.client.impl.module.world;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ScaffoldModule extends ToggleModule
{
    /**
     *
     */
    public ScaffoldModule()
    {
        super("Scaffold", "Automatically places block below the player",
                ModuleCategory.WORLD);
    }
}
