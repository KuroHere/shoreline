package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.rotation.RotationHandler;
import com.caspian.client.api.handler.rotation.RotationRequest;
import com.caspian.client.api.module.Module;
import net.minecraft.util.math.MathHelper;

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

    /**
     *
     *
     * @param requester
     * @param priority
     * @param yaw
     * @param pitch
     */
    public void setRotation(final Module requester,
                            final int priority,
                            final float yaw,
                            final float pitch)
    {
        handler.request(requester, priority, yaw, pitch);
    }
    /**
     *
     *
     * @param requester
     * @param yaw
     * @param pitch
     */
    public void setRotation(final Module requester,
                            final float yaw,
                            final float pitch)
    {
        handler.request(requester, yaw, pitch);
    }

    /**
     *
     *
     * @param request
     */
    public void removeRotation(final RotationRequest request)
    {
        handler.remove(request);
    }

    /**
     *
     *
     * @param requester
     */
    public void removeRotation(final Module requester)
    {
        handler.remove(requester);
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
    public float getWrappedYaw()
    {
        return MathHelper.wrapDegrees(handler.getYaw());
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

    /**
     *
     *
     * @return
     */
    public RotationRequest getCurrentRotation()
    {
        return handler.getLatestRequest();
    }

    /**
     *
     *
     * @return
     */
    public Module getRotatingModule()
    {
        return handler.getRotatingModule();
    }
}
