package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.StringConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.text.TextVisitEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NameProtectModule extends ToggleModule
{
    //
    Config<String> placeholderConfig = new StringConfig("Placeholder", "The " +
            "placeholder name for the player", "Player");

    /**
     *
     */
    public NameProtectModule()
    {
        super("NameProtect", "Hides the player name in chat and tablist",
                ModuleCategory.RENDER);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTextVisit(TextVisitEvent event)
    {
        if (mc.player == null)
        {
            return;
        }
        final String username = mc.getSession().getUsername();
        final String text = event.getText();
        if (text.contains(username))
        {
            event.cancel();
            event.setText(text.replace(username, placeholderConfig.getValue()));
        }
    }
}
