package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ChestAuraModule extends ToggleModule
{
    //
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range to " +
            "automatically open chests", 0.1f, 4.5f, 5.0f);
    Config<Boolean> onGroundConfig = new BooleanConfig("OnGround", "Allows " +
            "chests to be opened only if the player is on the ground", true);

    /**
     *
     */
    public ChestAuraModule()
    {
        super("ChestAura", "Automatically opens nearby chest containers",
                ModuleCategory.MISCELLANEOUS);
    }
}
