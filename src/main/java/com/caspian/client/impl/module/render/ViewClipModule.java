package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.render.CameraClipEvent;

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
    Config<Float> distanceConfig = new NumberConfig<>("Distance", "Third-person" +
            " camera distance", 1.0f, 3.5f, 20.0f);

    /**
     *
     */
    public ViewClipModule()
    {
        super("ViewClip", "Clips your third-person camera through blocks",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onCameraClip(CameraClipEvent event)
    {
        event.cancel();
        event.setDistance(distanceConfig.getValue());
    }
}
