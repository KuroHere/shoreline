package com.caspian.client.api.handler;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.Globals;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PositionHandler implements Globals
{
    //
    private double x, y, z;
    //
    private boolean sneaking, sprinting;
    //
    private boolean onGround;

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
                onGround = packet.isOnGround();
                if (packet.changesPosition())
                {
                    x = packet.getX(x);
                    y = packet.getY(y);
                    z = packet.getZ(z);
                }
            }
            else if (event.getPacket() instanceof ClientCommandC2SPacket packet)
            {
                switch (packet.getMode())
                {
                    case START_SPRINTING -> sprinting = true;
                    case STOP_SPRINTING -> sprinting = false;
                    case PRESS_SHIFT_KEY -> sneaking = true;
                    case RELEASE_SHIFT_KEY -> sneaking = false;
                }
            }
        }
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    /**
     *
     *
     * @return
     */
    public boolean isSneaking()
    {
        return sneaking;
    }

    /**
     *
     *
     * @return
     */
    public boolean isSprinting()
    {
        return sprinting;
    }

    /**
     *
     * @return
     */
    public boolean isOnGround()
    {
        return onGround;
    }
}
