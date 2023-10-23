package com.caspian.client.impl.module.render;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.gui.hud.PlayerListEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ExtraTabModule extends ToggleModule
{
    /**
     *
     */
    public ExtraTabModule()
    {
        super("ExtraTab", "Expands the tab list size to allow for more players",
                ModuleCategory.RENDER);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPlayerList(PlayerListEvent event)
    {
        event.cancel();
    }
}
