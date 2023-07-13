package com.caspian.client.api.handler;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.Globals;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.Timer;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NCPHandler implements Globals
{
    //
    private double x, y, z;
    private boolean lag;
    private final Timer lastRubberband = new CacheTimer();

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
     *
     * @param time
     * @return
     */
    public boolean passedSinceRubberband(long time)
    {
        return lastRubberband.passed(time);
    }


    /**
     *
     *
     * @return
     */
    public long timeSinceRubberband()
    {
        return lastRubberband.getElapsedTime();
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
}
