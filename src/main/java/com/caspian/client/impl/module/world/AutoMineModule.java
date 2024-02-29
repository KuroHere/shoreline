package com.caspian.client.impl.module.world;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author Shoreline
 * @since 1.0
 */
public class AutoMineModule extends ToggleModule
{
    public AutoMineModule()
    {
        super("AutoMine", "Automatically mines enemy blocks", ModuleCategory.WORLD);
    }
}
