package com.caspian.client.impl.module.combat;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BedAuraModule extends ToggleModule
{
    /**
     *
     */
    public BedAuraModule()
    {
        super("BedAura", "Automatically places and explodes beds",
                ModuleCategory.COMBAT);
    }
}
