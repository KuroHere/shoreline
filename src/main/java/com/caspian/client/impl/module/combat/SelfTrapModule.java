package com.caspian.client.impl.module.combat;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SelfTrapModule extends ToggleModule
{
    /**
     *
     */
    public SelfTrapModule()
    {
        super("SelfTrap", "Fully surrounds the player with blocks",
                ModuleCategory.COMBAT);
    }
}
