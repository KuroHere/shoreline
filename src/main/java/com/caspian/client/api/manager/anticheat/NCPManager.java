package com.caspian.client.api.manager.anticheat;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.Globals;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.Timer;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NCPManager implements Timer, Globals
{
    //
    private double x, y, z;
    private boolean lag;
    private final Timer lastRubberband = new CacheTimer();
    //
    private boolean strict;

    /**
     *
     *
     */
    public NCPManager()
    {
        Caspian.EVENT_HANDLER.subscribe(this);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet)
            {
                Vec3d last = new Vec3d(x, y, z);
                x = packet.getX();
                y = packet.getY();
                z = packet.getZ();
                lag = last.squaredDistanceTo(x, y, z) <= 1.0;
                lastRubberband.reset();
            }
        }
    }


    /**
     *
     * @return
     */
    public boolean isStrict()
    {
        return strict;
    }

    /**
     *
     *
     * @param strict
     */
    public void setStrict(boolean strict)
    {
        this.strict = strict;
    }

    /**
     *
     *
     * @return
     */
    public boolean isInRubberband()
    {
        return lag;
    }

    /**
     * Returns <tt>true</tt> if the time since the last reset has exceeded
     * the param time.
     *
     * @param time The param time
     * @return <tt>true</tt> if the time since the last reset has exceeded
     * the param time
     */
    @Override
    public boolean passed(Number time)
    {
        return lastRubberband.passed(time);
    }

    /**
     * Resets the current elapsed time state of the timer and restarts the
     * timer from 0.
     */
    @Deprecated
    @Override
    public void reset()
    {
        // DEPRECATED
    }

    /**
     * Returns the elapsed time since the last reset of the timer.
     *
     * @return The elapsed time since the last reset
     */
    @Override
    public long getElapsedTime()
    {
        return lastRubberband.getElapsedTime();
    }

    /**
     *
     *
     * @param time
     */
    @Deprecated
    @Override
    public void setElapsedTime(Number time)
    {
        // DEPRECATED
    }
}
