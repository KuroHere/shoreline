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
    private final Map<Entity, List<TrackedData>> trackedData =
            Collections.synchronizedMap(new ConcurrentHashMap<>());

    /**
     *
     *
     * @param player
     * @param data
     */
    public void addTrackedData(final PlayerEntity player,
                               final TrackedData data)
    {
        cleanCache();
        List<TrackedData> tracked = trackedData.computeIfAbsent(player,
                d -> new ArrayList<>());
        tracked.add(data);
    }

    /**
     *
     *
     */
    public void cleanCache()
    {
        final Collection<List<TrackedData>> data = trackedData.values();
        for (List<TrackedData> tracked : data)
        {
            for (TrackedData d : tracked)
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
    public TrackedData getTrackedData(final Vec3d floor,
                                      final PlayerEntity player,
                                      final long time)
    {
        TrackedData backtrack = null;
        final List<TrackedData> tracked = trackedData.get(player);
        if (!tracked.isEmpty())
        {
            double min = Double.MAX_VALUE;
            for (TrackedData d : tracked)
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
