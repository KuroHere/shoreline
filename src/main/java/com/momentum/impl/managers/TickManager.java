package com.momentum.impl.managers;

import com.momentum.Momentum;
import com.momentum.api.event.Listener;
import com.momentum.api.util.Wrapper;
import com.momentum.asm.mixins.vanilla.accessors.INetHandlerPlayClient;
import com.momentum.impl.events.vanilla.network.InboundPacketEvent;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Manages server ticks
 *
 * @author linus
 * @since 02/11/2023
 */
public class TickManager implements Wrapper {

    // time since last tick
    private long time;
    private float last;

    // server tps
    private float tps;

    // last 20 updates
    private final Queue<Float> times = new ArrayDeque<>(20);

    /**
     * Manages server ticks
     */
    public TickManager() {

        // tick impl
        Momentum.EVENT_BUS.subscribe(new Listener<InboundPacketEvent>() {

            @Override
            public void invoke(InboundPacketEvent event) {

                // null check
                if (mc.player == null || mc.world == null || !((INetHandlerPlayClient) mc.player.connection).isDoneLoadingTerrain()) {
                    return;
                }

                // packet for world time updates
                if (event.getPacket() instanceof SPacketTimeUpdate) {

                    // ticks passed since last world update
                    last = 20000f / (System.currentTimeMillis() - time);

                    // add to queue
                    times.add(last);

                    // tps is average of times
                    tps = average(times);

                    // reset time since last tick
                    time = System.currentTimeMillis();
                }
            }
        });
    }

    /**
     * Gets the average of the times
     *
     * @param times The times to average
     * @return The average of the times
     */
    private float average(Queue<Float> times) {

        // average time
        float avg = 0;

        // add times to average
        for (float t : times) {

            // clamp time
            avg += MathHelper.clamp(t, 0, 20);
        }

        // make sure times size isn't 0
        if (!times.isEmpty()) {

            // divide by total tomes
            avg /= times.size();
        }

        // avg
        return avg;
    }

    /**
     * Gets the tps
     *
     * @return The tps
     */
    public float getTps() {
        return tps;
    }

    /**
     * Gets the latest time
     *
     * @return The latest time
     */
    public float getLast() {
        return time;
    }
}
