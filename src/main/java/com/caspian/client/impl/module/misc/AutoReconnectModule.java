package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see com.caspian.client.mixin.gui.screen.MixinDisconnectedScreen
 */
public class AutoReconnectModule extends ToggleModule
{
    //
    Config<Float> delayConfig = new NumberConfig<>("Delay", "The delay " +
            "between reconnects to a server", 0.0f, 5.0f, 100.0f);

    /**
     *
     */
    public AutoReconnectModule()
    {
        super("AutoReconnect", "Automatically reconnects to a server " +
                "immediately after disconnecting", ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @return
     */
    public float getDelay()
    {
        return delayConfig.getValue();
    }
}
