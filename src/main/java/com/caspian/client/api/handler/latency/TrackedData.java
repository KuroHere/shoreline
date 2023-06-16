package com.caspian.client.api.handler.latency;

import com.caspian.client.util.Globals;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TrackedData implements Position, Globals
{
    // The tracking entity
    private final PlayerEntity track;
    //
    private final Vec3d pos;
    private final long time;

    /**
     *
     *
     * @param track
     * @param pos
     * @param time
     */
    public TrackedData(PlayerEntity track, Vec3d pos, long time)
    {
        this.track = track;
        this.pos = pos;
        this.time = time;
    }

    /**
     *
     * @return
     */
    public PlayerEntity getTrack()
    {
        return track;
    }

    /**
     *
     *
     * @return
     */
    public OtherClientPlayerEntity toTrackedEntity()
    {
        final OtherClientPlayerEntity tracked =
                new OtherClientPlayerEntity(mc.world, track.getGameProfile())
                {
                    /**
                     *
                     * @return
                     */
                    @Override
                    public boolean isDead()
                    {
                        return false;
                    }
                };
        tracked.copyPositionAndRotation(track);
        tracked.getInventory().clone(track.getInventory());
        tracked.setId(track.getId());
        mc.world.spawnEntity(tracked);
        mc.world.addEntity(track.getId(), tracked);
        return tracked;
    }

    /**
     * Returns the X coordinate.
     */
    @Override
    public double getX()
    {
        return pos.getX();
    }

    /**
     * Returns the Y coordinate.
     */
    @Override
    public double getY()
    {
        return pos.getY();
    }

    /**
     * Returns the Z coordinate.
     */
    @Override
    public double getZ()
    {
        return pos.getZ();
    }

    /**
     *
     *
     * @return
     */
    public long getTime()
    {
        return System.currentTimeMillis() - time;
    }
}
