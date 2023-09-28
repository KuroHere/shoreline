package com.caspian.client.impl.module.render;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 */
public class SkyboxModule extends ToggleModule
{

    /**
     *
     */
    public SkyboxModule()
    {
        super("Skybox", "Changes the rendering of the world skybox",
                ModuleCategory.RENDER);
    }
}
