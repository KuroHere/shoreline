package com.caspian.client.api.config.setting;

import com.caspian.client.Caspian;
import com.caspian.client.api.config.ConfigContainer;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see BooleanConfig
 */
public class ToggleConfig extends BooleanConfig
{
    public ToggleConfig(String name, String desc, Boolean val)
    {
        super(name, desc, val);
    }

    /**
     *
     * @param val The param value
     */
    @Override
    public void setValue(Boolean val)
    {
        super.setValue(val);
        ConfigContainer container = getContainer();
        if (container instanceof ToggleModule toggle)
        {
            if (val)
            {
                Caspian.EVENT_HANDLER.subscribe(toggle);
            }
            else
            {
                Caspian.EVENT_HANDLER.unsubscribe(toggle);
            }
        }
    }
}
