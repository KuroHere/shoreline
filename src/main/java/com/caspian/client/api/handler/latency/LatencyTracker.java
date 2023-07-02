package com.caspian.client.api.handler.latency;

import com.caspian.client.util.Globals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class LatencyTracker implements Globals
{
    // BACKTRACK MANAGER
    private final Map<Entity, List<PlayerLatency>> trackedData =
            Collections.synchronizedMap(new ConcurrentHashMap<>());

    /**
     *
     *
     * @param player
     * @param data
     */
    public void addTrackedData(final PlayerEntity player,
                               final PlayerLatency data)
    {
        cleanCache();
        List<PlayerLatency> tracked = trackedData.computeIfAbsent(player,
                d -> new ArrayList<>());
        tracked.add(data);
    }

    /**
     *
     *
     */
    public void cleanCache()
    {
        final Collection<List<PlayerLatency>> data = trackedData.values();
        for (List<PlayerLatency> tracked : data)
        {
            for (PlayerLatency d : tracked)
            {
                if (d.getTime() > 1000L)
                {
                    trackedData.remove(d.getTrack());
                }
            }
        }
    }

    /**
     *
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
        PlayerLatency backtrack = null;
        final List<PlayerLatency> tracked = trackedData.get(player);
        if (!tracked.isEmpty())
        {
            double min = Double.MAX_VALUE;
            for (PlayerLatency d : tracked)
            {
                if (d.getTime() > time)
                {
                    continue;
                }
                double dist = floor.squaredDistanceTo(d.getX(), d.getY(),
                        d.getZ());
                if (dist < min)
                {
                    min = dist;
                    backtrack = d;
                }
            }
        }
        return backtrack;
    }
}
