package com.caspian.client.impl.event.render;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

import java.awt.*;

@Cancelable
public class AmbientColorEvent extends Event
{
    private Color color;

    public void setColor(Color color)
    {
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }
}
