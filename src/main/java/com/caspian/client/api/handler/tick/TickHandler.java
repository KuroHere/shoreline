package com.caspian.client.api.handler.tick;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.Globals;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TickHandler implements Globals
{
    //
    private long time;
    private final Queue<Float> ticks = new ArrayDeque<>(20);

    /**
     *
     *
     * @param event
     *
     * @see WorldTimeUpdateS2CPacket
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof WorldTimeUpdateS2CPacket)
            {
                float last = 20000.0f / (System.currentTimeMillis() - time);
                ticks.add(last);
                time = System.currentTimeMillis();
            }
        }
    }

    /**
     *
     *
     * @return
     */
    public Queue<Float> getTicks()
    {
        return ticks;
    }

    /**
     *
     *
     * @return
     */
    public boolean hasTicks()
    {
        return !ticks.isEmpty();
    }

    /**
     *
     * @return
     */
    public Float poll()
    {
        return ticks.poll();
    }

    /**
     *
     * @return
     */
    public Float peek()
    {
        return ticks.peek();
    }

    /**
     *
     *
     * @return
     */
    public int size()
    {
        return ticks.size();
    }
}
