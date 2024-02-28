package com.caspian.client.impl.module.combat;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoXPModule extends ToggleModule
{

    public AutoXPModule()
    {
        super("AutoXP", "Automatically mends armor using XP bottles",
                ModuleCategory.COMBAT);
    }
}
