package com.caspian.client.impl.module.client;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.module.ConcurrentModule;
import com.caspian.client.api.module.ModuleCategory;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ChatModule extends ConcurrentModule
{
    //
    Config<Boolean> debugConfig = new BooleanConfig("ChatDebug", "Allows " +
            "client debug messages to be printed in the chat", false);

    /**
     *
     */
    public ChatModule()
    {
        super("Chat", "Manages the client chat", ModuleCategory.CLIENT);
    }
}
