package com.caspian.client.impl.module.misc;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AnnouncerModule extends ToggleModule
{
    /**
     *
     */
    public AnnouncerModule()
    {
        super("Announcer", "Announces player actions in the chat",
                ModuleCategory.MISCELLANEOUS);
    }
}
