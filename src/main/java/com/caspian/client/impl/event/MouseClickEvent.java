package com.caspian.client.impl.event;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

@Cancelable
public class MouseClickEvent extends Event
{
    private final int button;
    private final int action;

    public MouseClickEvent(int button, int action)
    {
        this.button = button;
        this.action = action;
    }

    public int getButton()
    {
        return button;
    }

    public int getAction()
    {
        return action;
    }
}
