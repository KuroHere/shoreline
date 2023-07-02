package com.caspian.client.api.handler.rotation;

import com.caspian.client.api.module.Module;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class RotationRequest
{
    //
    private final Module requester;
    private final long time;
    private final int priority;
    //
    private float yaw, pitch;

    /**
     *
     *
     * @param requester
     * @param priority
     * @param yaw
     * @param pitch
     */
    public RotationRequest(Module requester,
                           int priority,
                           float yaw,
                           float pitch)
    {
        this.requester = requester;
        this.time = System.currentTimeMillis();
        this.priority = priority;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     *
     *
     * @param requester
     * @param yaw
     * @param pitch
     */
    public RotationRequest(Module requester,
                           float yaw,
                           float pitch)
    {
        this(requester, 100, yaw, pitch);
    }

    public Module getRequester()
    {
        return requester;
    }

    public float getYaw()
    {
        return yaw;
    }

    public float getPitch()
    {
        return pitch;
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }
}
