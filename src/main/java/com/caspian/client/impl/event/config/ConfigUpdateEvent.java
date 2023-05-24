package com.caspian.client.impl.event.config;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.event.StageEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ConfigUpdateEvent extends StageEvent
{
    //
    private final Config<?> config;

    /**
     *
     *
     * @param config
     */
    public ConfigUpdateEvent(Config<?> config)
    {
        this.config = config;
    }

    /**
     *
     * @return
     */
    public Config<?> getConfig()
    {
        return config;
    }
}
