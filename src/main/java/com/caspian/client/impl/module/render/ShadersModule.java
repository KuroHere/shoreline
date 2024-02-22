package com.caspian.client.impl.module.render;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ShadersModule extends ToggleModule
{
    /**
     *
     */
    public ShadersModule()
    {
        super("Shaders", "Renders shaders in-game", ModuleCategory.RENDER);
    }
}
