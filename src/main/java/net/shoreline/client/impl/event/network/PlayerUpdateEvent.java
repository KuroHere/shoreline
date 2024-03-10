package net.shoreline.client.impl.event.network;

import net.shoreline.client.api.event.StageEvent;

public class PlayerUpdateEvent extends StageEvent
{
    private float yaw;
    private float pitch;

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
