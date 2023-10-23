package com.caspian.client.impl.module.movement;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

// Is this still necessary in 1.19?
public class FastSwimModule extends ToggleModule
{
    /**
     *
     */
    public FastSwimModule()
    {
        super("FastSwim", "Allows the player to swim faster",
                ModuleCategory.MOVEMENT);
    }
}
