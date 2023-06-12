package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.latency.LatencyPositionHandler;
import com.caspian.client.api.handler.latency.PosTracker;
import com.caspian.client.api.handler.latency.TrackedData;
import net.minecraft.entity.player.PlayerEntity;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class LatencyPositionManager
{
    //
    private final LatencyPositionHandler handler;

    /**
     *
     */
    public LatencyPositionManager()
    {
        handler = new LatencyPositionHandler(new PosTracker());
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    /**
     *
     *
     * @param player
     * @param time
     * @return
     */
    public TrackedData getTrackedData(final PlayerEntity player,
                                      final long time)
    {
        return handler.getTrackedData(player, time);
    }
}
