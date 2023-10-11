package com.caspian.client.impl.event.network;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

@Cancelable
public class ReachEvent extends Event
{
    private float reach;

    public float getReach()
    {
        return reach;
    }

    public void setReach(float reach)
    {
        this.reach = reach;
    }
}
