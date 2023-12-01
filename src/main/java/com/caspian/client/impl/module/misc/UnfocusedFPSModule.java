package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.FramerateLimitEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class UnfocusedFPSModule extends ToggleModule
{
    //
    Config<Integer> limitConfig = new NumberConfig<>("Limit", "The FPS limit " +
            "when game is in the background", 5, 30, 120);

    /**
     *
     */
    public UnfocusedFPSModule()
    {
        super("UnfocusedFPS", "Reduces FPS when game is in the background",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onFramerateLimit(FramerateLimitEvent event)
    {
        if (!mc.isWindowFocused())
        {
            event.cancel();
            event.setFramerateLimit(limitConfig.getValue());
        }
    }
}
