package com.caspian.client.impl.module.combat;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoPotModule extends ToggleModule
{
    /**
     *
     */
    public AutoPotModule()
    {
        super("AutoPot", "Automatically throws beneficial potions",
                ModuleCategory.COMBAT);
    }
}
