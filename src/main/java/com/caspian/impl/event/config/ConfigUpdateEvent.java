package com.caspian.impl.event.config;

import com.caspian.api.config.Config;
import com.caspian.api.event.StageEvent;

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
