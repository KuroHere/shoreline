package com.caspian.client.impl.module.combat;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class HoleFillModule extends ToggleModule
{
    /**
     *
     */
    public HoleFillModule()
    {
        super("HoleFill", "Fills in nearby holes with blocks", ModuleCategory.COMBAT);
    }
}
