package com.caspian.client.impl.event.network;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

@Cancelable
public class MountJumpStrengthEvent extends Event
{
    //
    private float jumpStrength;

    public void setJumpStrength(float jumpStrength)
    {
        this.jumpStrength = jumpStrength;
    }

    public float getJumpStrength()
    {
        return jumpStrength;
    }
}
