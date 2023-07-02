package com.caspian.client.api.handler.rotation;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.event.render.RenderPlayerEvent;
import com.caspian.client.util.Globals;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

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
