package com.caspian.client.api.manager.rotation;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.RotationModule;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.event.render.entity.RenderPlayerEvent;
import com.caspian.client.init.Modules;
import com.caspian.client.util.Globals;
import com.caspian.client.util.math.timer.TickTimer;
import com.caspian.client.util.math.timer.Timer;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

import java.util.PriorityQueue;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class RotationManager implements Globals
{
    //
    private float yaw, pitch;
    //
    private RotationRequest rotation;
    private final PriorityQueue<RotationRequest> requests =
            new PriorityQueue<>();
    private final Timer rotateTimer = new TickTimer();

    /**
     *
     *
     */
    public RotationManager()
    {
        Caspian.EVENT_HANDLER.subscribe(this);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof PlayerMoveC2SPacket packet
                    && packet.changesLook())
            {
                yaw = packet.getYaw(yaw);
                pitch = packet.getPitch(pitch);
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onRenderPlayer(RenderPlayerEvent event)
    {
        if (event.getEntity() == mc.player && rotation != null)
        {
            event.setYaw(rotation.getYaw());
            event.setPitch(rotation.getPitch());
            event.cancel();
        }
    }

    /**
     *
     *
     * @param requester
     * @param priority
     * @param yaw
     * @param pitch
     */
    public void setRotation(final RotationModule requester,
                            final RotationPriority priority,
                            final float yaw,
                            final float pitch)
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
     * @param yaw
     * @param pitch
     */
    public void setRotation(final RotationModule requester,
                            final float yaw,
                            final float pitch)
    {
        setRotation(requester, RotationPriority.NORMAL, yaw, pitch);
    }

    /**
     *
     *
     * @param request
     */
    public boolean removeRotation(final RotationRequest request)
    {
        return requests.remove(request);
    }

    /**
     *
     *
     * @param requester
     */
    public void removeRotation(final RotationModule requester)
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
    public boolean isRotating()
    {
        return !rotateTimer.passed(Modules.ROTATIONS.getPreserveTicks());
    }

    /**
     *
     *
     * @return
     */
    public RotationRequest getCurrentRotation()
    {
        if (requests.size() <= 1 && isRotating())
        {
            rotation = requests.peek();
            return rotation;
        }
        else if (requests.size() > 1)
        {
            rotateTimer.reset();
            rotation = requests.poll();
            return rotation;
        }
        return null;
    }

    /**
     *
     *
     * @return
     */
    public RotationModule getRotatingModule()
    {
        if (rotation == null)
        {
            return null;
        }
        return rotation.getRequester();
    }

    /**
     *
     *
     * @return
     */
    public float getYaw()
    {
        return yaw;
    }

    /**
     *
     *
     * @return
     */
    public float getWrappedYaw()
    {
        return MathHelper.wrapDegrees(yaw);
    }

    /**
     *
     * @return
     */
    public float getPitch()
    {
        return pitch;
    }
}
