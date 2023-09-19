package com.caspian.client.api.manager.world.tick;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.EvictingQueue;
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
public class TickManager implements Globals
{
    // The TPS tick handler.
    //
    private long time;
    private final ArrayDeque<Float> ticks = new EvictingQueue<>(20);

    /**
     *
     *
     */
    public TickManager()
    {
        Caspian.EVENT_HANDLER.subscribe(this);
    }

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
        if (mc.player == null || mc.world == null)
        {
            return;
        }
        // ticks/actual
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket)
        {
            float last = 20000.0f / (System.currentTimeMillis() - time);
            ticks.addFirst(last);
            time = System.currentTimeMillis();
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
    public float getTpsAverage()
    {
        float avg = 0.0f;
        if (ticks.size() > 0)
        {
            for (float t : ticks)
            {
                avg += t;
            }
            avg /= ticks.size();
        }
        return avg;
    }

    /**
     *
     *
     * @return
     */
    public float getTpsCurrent()
    {
        if (!ticks.isEmpty())
        {
            return ticks.getFirst();
        }
        return 20.0f;
    }

    /**
     *
     *
     * @return
     */
    public float getTpsMin()
    {
        float min = 20.0f;
        for (float t : ticks)
        {
            if (t < min)
            {
                min = t;
            }
        }
        return min;
    }

    /**
     *
     *
     * @param tps
     * @return
     */
    public float getTickSync(TickSync tps)
    {
        return switch (tps)
                {
                    case AVERAGE -> getTpsAverage();
                    case CURRENT -> getTpsCurrent();
                    case MINIMAL -> getTpsMin();
                    case NONE -> 20.0f;
                };
    }
}
