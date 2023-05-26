package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.RotationHandler;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class RotationManager
{
    //
    private final RotationHandler handler;

    /**
     *
     *
     */
    public RotationManager()
    {
        handler = new RotationHandler();
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    public void setRotationClient(float yaw, float pitch)
    {

    }

    /**
     *
     *
     * @return
     */
    public float getYaw()
    {
        return handler.getYaw();
    }

    /**
     *
     *
     * @return
     */
    public float getPitch()
    {
        return handler.getPitch();
    }
}
