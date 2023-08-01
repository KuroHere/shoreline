package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.latency.LatencyPositionHandler;
import com.caspian.client.util.world.FakePlayerEntity;
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
        handler = new LatencyPositionHandler();
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    /**
     *
     *
     * @param floor
     * @param player
     * @param time
     * @return
     */
    public FakePlayerEntity getTrackedPlayer(final Vec3d floor,
                                             final PlayerEntity player,
                                             final long time)
    {
        return handler.getTrackedPlayer(floor, player, time);
    }

    /**
     *
     * @param floor
     * @param player
     * @param time
     * @return
     */
    public Vec3d getTrackedData(final Vec3d floor,
                                final PlayerEntity player,
                                final long time)
    {
        return handler.getTrackedData(floor, player, time);
    }
}
