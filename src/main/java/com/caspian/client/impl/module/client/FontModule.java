package com.caspian.client.impl.module.client;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 * @author linus
 * @since 1.0
 */
public class FontModule extends ToggleModule
{
    //
    Config<Boolean> shadowConfig = new BooleanConfig("Shadow", "Renders text " +
            "with a shadow background", true);

    /**
     *
     */
    public FontModule()
    {
        super("Font", "Changes the client text to custom font rendering",
                ModuleCategory.CLIENT);
    }

    /**
     *
     * @return
     */
    public boolean getShadow()
    {
        return shadowConfig.getValue();
    }
}
