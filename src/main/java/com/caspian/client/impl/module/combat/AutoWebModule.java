package com.caspian.client.impl.module.combat;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoWebModule extends ToggleModule
{
    /**
     *
     */
    public AutoWebModule()
    {
        super("AutoWeb", "Automatically traps nearby entities in webs",
                ModuleCategory.COMBAT);
    }
}
