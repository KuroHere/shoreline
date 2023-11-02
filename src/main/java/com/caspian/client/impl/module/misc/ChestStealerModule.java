package com.caspian.client.impl.module.misc;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ChestStealerModule extends ToggleModule
{
    /**
     *
     */
    public ChestStealerModule()
    {
        super("ChestStealer", "Steals valuable items from chests",
                ModuleCategory.MISCELLANEOUS);
    }
}
