package com.caspian.client.api.handler.latency;

import com.caspian.client.util.Globals;
import com.caspian.client.util.world.FakePlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PlayerLatency implements Position, Globals
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
    public PlayerLatency(PlayerEntity track, Vec3d pos, long time)
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
    public FakePlayerEntity getLatencyPlayer()
    {
        final FakePlayerEntity p = new FakePlayerEntity(track);
        p.setPosition(pos);
        return p;
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
