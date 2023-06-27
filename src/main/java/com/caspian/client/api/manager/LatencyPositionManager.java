package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.latency.LatencyPositionHandler;
import com.caspian.client.api.handler.latency.PosTracker;
import com.caspian.client.api.handler.latency.LatencyPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

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
     * @param floor
     * @param player
     * @param time
     * @return
     */
    public LatencyPlayer getTrackedData(final Vec3d floor,
                                        final PlayerEntity player,
                                        final long time)
    {
        return handler.getTrackedData(floor, player, time);
    }
}
