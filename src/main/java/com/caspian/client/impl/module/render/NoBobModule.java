package com.caspian.client.impl.module.render;

import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.render.BobViewEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NoBobModule extends ToggleModule
{
    /**
     *
     */
    public NoBobModule()
    {
        super("NoBob", "Prevents the hand from bobbing while moving",
                ModuleCategory.RENDER);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onBobView(BobViewEvent event)
    {
        event.cancel();
    }
}
