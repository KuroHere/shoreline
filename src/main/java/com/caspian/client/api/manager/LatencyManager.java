package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.latency.LatencyPositionHandler;
import com.caspian.client.api.handler.latency.LatencyTracker;
import com.caspian.client.api.handler.latency.PlayerLatency;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class LatencyManager
{
    //
    private final LatencyPositionHandler handler;

    /**
     *
     */
    public LatencyManager()
    {
        handler = new LatencyPositionHandler(new LatencyTracker());
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    /**
     *
     * @param floor
     * @param player
     * @param time
     * @return
     */
    public PlayerLatency getTrackedData(final Vec3d floor,
                                        final PlayerEntity player,
                                        final long time)
    {
        return handler.getTrackedData(floor, player, time);
    }
}
