package com.caspian.impl.gui.click.impl.config.setting;

import com.caspian.api.config.Config;
import com.caspian.impl.gui.click.component.Button;
import com.caspian.impl.gui.click.impl.config.ConfigFrame;

/**
 *
 * @author linus
 * @since 1.0
 *
 * @param <T>
 */
public abstract class ConfigComponent<T> extends Button
{
    //
    private final Config<T> config;

    /**
     *
     *
     * @param frame
     * @param config
     */
    public ConfigComponent(ConfigFrame frame, Config<T> config)
    {
        super(frame);
        this.config = config;
    }

    /**
     *
     *
     * @return
     */
    public Config<T> getConfig()
    {
        return config;
    }
}
