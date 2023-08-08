package com.caspian.client.impl.module.client;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.ConfigContainer;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.ListConfig;
import com.caspian.client.api.module.ConcurrentModule;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    Config<List<ConfigContainer>> debugListConfig = new ListConfig<>(
            "DebugList", "The chat debug feature whitelist",
            Collections.emptyList());

    /**
     *
     */
    public ChatModule()
    {
        super("Chat", "Manages the client chat", ModuleCategory.CLIENT);
    }

    /**
     *
     *
     */
    @Override
    public void onLoad()
    {
        if (Managers.isInitialized() && Modules.isInitialized())
        {
            debugListConfig.setValue(Arrays.asList(
                    Modules.ROTATIONS,
                    Modules.AUTO_TOTEM
            ));
        }
    }
}
