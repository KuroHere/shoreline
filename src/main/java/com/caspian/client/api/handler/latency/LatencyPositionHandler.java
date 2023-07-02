package com.caspian.client.api.handler.latency;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.Globals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class LatencyPositionHandler implements Globals
{
    //
    private final LatencyTracker tracker;

    /**
     *
     *
     * @param tracker
     */
    public LatencyPositionHandler(LatencyTracker tracker)
    {
        this.tracker = tracker;
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
        return tracker.getTrackedData(floor, player, time);
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
            if (event.getPacket() instanceof EntityPositionS2CPacket packet)
            {
                Entity e = mc.world.getEntityById(packet.getId());
                if (e instanceof PlayerEntity player)
                {
                    tracker.addTrackedData(player, new PlayerLatency(player,
                            new Vec3d(packet.getX(), packet.getY(),
                            packet.getZ()), System.currentTimeMillis()));
                }
            }
            else if (event.getPacket() instanceof EntitySpawnS2CPacket packet)
            {
                Entity e = mc.world.getEntityById(packet.getId());
                if (packet.getEntityType() == EntityType.PLAYER)
                {
                    PlayerEntity player = (PlayerEntity) e;
                    tracker.addTrackedData(player, new PlayerLatency(player,
                            new Vec3d(packet.getX(), packet.getY(),
                                    packet.getZ()), System.currentTimeMillis()));
                }
            }
        }
    }
}
