package com.caspian.client.impl.event.gui.click;

import com.caspian.client.api.event.Event;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 * @author linus
 * @since 1.0
 */
public class ToggleGuiEvent extends Event
{
    private final ToggleModule module;

    public ToggleGuiEvent(ToggleModule module)
    {
        this.module = module;
    }

    public ToggleModule getModule()
    {
        return module;
    }

    public boolean isEnabled()
    {
        return module.isEnabled();
    }
}
