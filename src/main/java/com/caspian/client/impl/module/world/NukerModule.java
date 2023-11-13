package com.caspian.client.impl.module.world;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NukerModule extends ToggleModule
{
    /**
     *
     */
    public NukerModule()
    {
        super("Nuker", "Destroys all blocks around the player", ModuleCategory.WORLD);
    }
}
