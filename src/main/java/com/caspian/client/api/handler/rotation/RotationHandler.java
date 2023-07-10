package com.caspian.client.api.handler.rotation;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.Module;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.event.render.RenderPlayerEvent;
import com.caspian.client.init.Modules;
import com.caspian.client.util.Globals;
import com.caspian.client.util.math.timer.TickTimer;
import com.caspian.client.util.math.timer.Timer;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.PriorityQueue;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class RotationHandler implements Globals
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
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof PlayerMoveC2SPacket packet)
            {
                if (packet.changesLook())
                {
                    yaw = packet.getYaw(yaw);
                    pitch = packet.getPitch(pitch);
                }
            }
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
    public void request(final Module requester,
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
    public void request(Module requester, float yaw, float pitch)
    {
        request(requester, RotationPriority.NORMAL, yaw, pitch);
    }

    /**
     *
     *
     * @return
     */
    public boolean preserveRotations()
    {
        return !rotateTimer.passed(Modules.ROTATIONS.getPreserveTicks());
    }

    /**
     *
     *
     * @return
     */
    public RotationRequest getLatestRequest()
    {
        if (requests.size() <= 1 && preserveRotations())
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
    public Module getRotatingModule()
    {
        return rotation.getRequester();
    }

    /**
     *
     *
     * @param requester
     */
    public void remove(final Module requester)
    {
        requests.removeIf(r -> requester == r.getRequester());
    }

    /**
     *
     *
     * @param request
     * @return
     */
    public boolean remove(RotationRequest request)
    {
        return requests.remove(request);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onRenderPlayer(RenderPlayerEvent event)
    {
        if (event.getEntity() == mc.player)
        {
            event.setYaw(yaw);
            event.setPitch(pitch);
            event.cancel();
        }
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
     * @return
     */
    public float getPitch()
    {
        return pitch;
    }
}
