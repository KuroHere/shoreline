package com.caspian.client.impl.module.combat;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.PlaceBlockModule;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoTrapModule extends PlaceBlockModule
{
    /**
     *
     */
    public AutoTrapModule()
    {
        super("AutoTrap", "Automatically traps nearby players in blocks",
                ModuleCategory.COMBAT);
    }
}
