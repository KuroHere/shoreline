package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.rotation.RotationHandler;
import com.caspian.client.api.handler.rotation.RotationRequest;
import com.caspian.client.api.module.Module;
import net.minecraft.util.math.MathHelper;

import java.util.PriorityQueue;

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
    //
    private final PriorityQueue<RotationRequest> requests =
            new PriorityQueue<>();
    private Module rotation;

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
                            float yaw,
                            float pitch)
    {
        for (RotationRequest r : requests)
        {
            if (requester == r.getRequester())
            {
                r.setYaw(yaw);
                r.setPitch(pitch);
                return;
            }
        }
        requests.add(new RotationRequest(requester, priority, yaw, pitch));
    }

    /**
     *
     *
     * @param requester
     */
    public void removeRotation(final Module requester)
    {
        requests.removeIf(r -> requester == r.getRequester());
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
    public Module getCurrentRotation()
    {
        return rotation;
    }
}
