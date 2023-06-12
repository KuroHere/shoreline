package com.caspian.client.api.handler.latency;

import com.caspian.client.util.Globals;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PosTracker implements Globals
{
    // BACKTRACK MANAGER
    private final Map<Entity, TrackEntity> trackedData =
            Collections.synchronizedMap(new ConcurrentHashMap<>());

    /**
     *
     *
     * @param data
     */
    public void addTrackedData(final PlayerEntity player,
                                final TrackedData data)
    {
        TrackEntity tracked = trackedData.computeIfAbsent(player,
                        d -> {
                            final TrackEntity tracker = new TrackEntity(mc.world,
                                player.getGameProfile());
                            tracker.refreshPositionAndAngles(data.getX(),
                                    data.getY(), data.getZ(), player.getYaw(), player.getPitch());
                            tracker.getInventory().clone(player.getInventory());
                            tracker.setId(player.getId());
                            mc.world.spawnEntity(tracker);
                            mc.world.addEntity(player.getId(), tracker);
                            return tracker;
                        });
        tracked.cleanCache();
        tracked.add(data);
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
        TrackedData backtrack = null;
        TrackEntity tracked = trackedData.get(player);
        if (!tracked.isDataEmpty())
        {
            long minDiff = Long.MAX_VALUE;
            for (TrackedData d : tracked.getData())
            {
                long diff = Math.abs(d.getTime() - time);
                if (diff < minDiff)
                {
                    minDiff = diff;
                    backtrack = d;
                }
            }
        }
        if (backtrack != null)
        {
            tracked.setPos(backtrack.getX(), backtrack.getY(), backtrack.getZ());
        }
        return backtrack;
    }

    /**
     *
     *
     * @author linus
     * @since 1.0
     */
    private class TrackEntity extends OtherClientPlayerEntity
    {
        //
        private final List<TrackedData> posdata = new CopyOnWriteArrayList<>();

        /**
         *
         *
         * @param clientWorld
         * @param gameProfile
         */
        public TrackEntity(ClientWorld clientWorld, GameProfile gameProfile)
        {
            super(clientWorld, gameProfile);
        }

        /**
         *
         *
         * @param data
         * @return
         */
        public boolean add(final TrackedData data)
        {
            return posdata.add(data);
        }

        /**
         *
         *
         * @return
         */
        public boolean isDataEmpty()
        {
            return posdata.isEmpty();
        }

        /**
         *
         *
         * @return
         */
        public List<TrackedData> getData()
        {
            return posdata;
        }

        /**
         *
         *
         */
        public void cleanCache()
        {
            for (TrackedData data : posdata)
            {
                if (data.getTime() > 1000L)
                {
                    trackedData.remove(data.getTrack());
                }
            }
        }

        /**
         *
         *
         * @return
         */
        @Override
        public boolean isDead()
        {
            return false;
        }
    }
}
