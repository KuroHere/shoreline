package com.caspian.client.impl.module.world;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class WallhackModule extends ToggleModule
{
    /**
     *
     */
    public WallhackModule()
    {
        super("Wallhack", "Allows you to see through solid blocks",
                ModuleCategory.WORLD);
    }
}
