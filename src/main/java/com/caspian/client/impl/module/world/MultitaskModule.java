package com.caspian.client.impl.module.world;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.ItemMultitaskEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class MultitaskModule extends ToggleModule
{
    /**
     *
     */
    public MultitaskModule()
    {
        super("MultiTask", "Allows you to mine and use items simultaneously",
                ModuleCategory.WORLD);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onItemMultitask(ItemMultitaskEvent event)
    {
        event.cancel();
    }
}
