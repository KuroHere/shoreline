package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.PlaceBlockModule;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class HoleFillModule extends PlaceBlockModule
{
    //
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range" +
            " to fill nearby holes", 0.1f, 4.0f, 5.0f);

    /**
     *
     */
    public HoleFillModule()
    {
        super("HoleFill", "Fills in nearby holes with blocks", ModuleCategory.COMBAT);
    }


}
