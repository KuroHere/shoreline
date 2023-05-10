package com.caspian.impl.event.network;

import com.caspian.api.event.StageEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 *
 */
public class MovementPacketsEvent extends StageEvent
{
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public float getYaw()
    {
        return yaw;
    }

    public float getPitch()
    {
        return pitch;
    }

    public boolean getOnGround()
    {
        return onGround;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround)
    {
        this.onGround = onGround;
    }
}
