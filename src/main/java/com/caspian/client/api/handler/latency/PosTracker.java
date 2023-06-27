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
public class PosTracker implements Globals
{
    // BACKTRACK MANAGER
    private final Map<Entity, List<LatencyPlayer>> trackedData =
            Collections.synchronizedMap(new ConcurrentHashMap<>());

    /**
     *
     *
     * @param player
     * @param data
     */
    public void addTrackedData(final PlayerEntity player,
                               final LatencyPlayer data)
    {
        cleanCache();
        List<LatencyPlayer> tracked = trackedData.computeIfAbsent(player,
                d -> new ArrayList<>());
        tracked.add(data);
    }

    /**
     *
     *
     */
    public void cleanCache()
    {
        final Collection<List<LatencyPlayer>> data = trackedData.values();
        for (List<LatencyPlayer> tracked : data)
        {
            for (LatencyPlayer d : tracked)
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
    public LatencyPlayer getTrackedData(final Vec3d floor,
                                        final PlayerEntity player,
                                        final long time)
    {
        LatencyPlayer backtrack = null;
        final List<LatencyPlayer> tracked = trackedData.get(player);
        if (!tracked.isEmpty())
        {
            double min = Double.MAX_VALUE;
            for (LatencyPlayer d : tracked)
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
