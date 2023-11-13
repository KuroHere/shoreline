package com.caspian.client.impl.module.misc;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SpammerModule extends ToggleModule
{
    /**
     *
     */
    public SpammerModule()
    {
        super("Spammer", "Spams messages in the chat", ModuleCategory.MISCELLANEOUS);
    }
}
