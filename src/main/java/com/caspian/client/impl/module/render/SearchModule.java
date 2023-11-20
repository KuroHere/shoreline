package com.caspian.client.impl.module.render;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SearchModule extends ToggleModule
{
    /**
     *
     */
    public SearchModule()
    {
        super("Search", "Highlights specified blocks in the world",
                ModuleCategory.RENDER);
    }
}
