package com.caspian.impl.module.render;

import com.caspian.api.config.Config;
import com.caspian.api.config.setting.NumberConfig;
import com.caspian.api.module.ModuleCategory;
import com.caspian.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 */
public class ViewClipModule extends ToggleModule
{
    //
    Config<Float> distance = new NumberConfig<>("Distance", "Third person FOV",
            1.0f, 3.5f, 20.0f);

    /**
     *
     */
    public ViewClipModule()
    {
        super("ViewClip", "Clips your third person camera through blocks",
                ModuleCategory.MISCELLANEOUS);
    }
}
