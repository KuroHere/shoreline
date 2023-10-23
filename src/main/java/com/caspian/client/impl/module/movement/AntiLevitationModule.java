package com.caspian.client.impl.module.movement;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.entity.LevitationEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiLevitationModule extends ToggleModule
{
    /**
     *
     */
    public AntiLevitationModule()
    {
        super("AntiLevitation", "Prevents the player from being levitated",
                ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onLevitation(LevitationEvent event)
    {
        event.cancel();
    }
}
