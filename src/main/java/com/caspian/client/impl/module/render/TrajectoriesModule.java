package com.caspian.client.impl.module.render;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TrajectoriesModule extends ToggleModule
{
    /**
     *
     */
    public TrajectoriesModule()
    {
        super("Trajectories", "Renders the trajectory path of projectiles",
                ModuleCategory.RENDER);
    }
}
