package com.caspian.client.impl.module.world;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiInteractModule extends ToggleModule
{
    public AntiInteractModule()
    {
        super("AntiInteract", "Prevents player from " +
                "interacting with certain objects", ModuleCategory.WORLD);
    }
}
