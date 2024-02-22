package com.caspian.client.api.manager.player.rotation;

import com.caspian.client.api.module.RotationModule;

/**
 *
 *
 * @author linus, bon55
 * @since 1.0
 */
public class RotationRequest
{
    //
    private final RotationModule requester;
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
    public RotationRequest(RotationModule requester,
                           RotationPriority priority,
                           float yaw,
                           float pitch)
    {
        this.requester = requester;
        this.time = System.currentTimeMillis();
        this.priority = priority.getPriority();
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
    public RotationRequest(RotationModule requester,
                           float yaw,
                           float pitch)
    {
        this(requester, RotationPriority.NORMAL, yaw, pitch);
    }

    public RotationModule getRequester()
    {
        return requester;
    }

    /**
     *
     * @return
     */
    public long getTime()
    {
        return System.currentTimeMillis() - time;
    }

    public int getPriority()
    {
        return priority;
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
