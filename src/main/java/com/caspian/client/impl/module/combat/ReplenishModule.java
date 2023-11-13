package com.caspian.client.impl.module.combat;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ReplenishModule extends ToggleModule
{
    /**
     *
     */
    public ReplenishModule()
    {
        super("Replenish", "Automatically replaces items in your hotbar",
                ModuleCategory.COMBAT);
    }
}
