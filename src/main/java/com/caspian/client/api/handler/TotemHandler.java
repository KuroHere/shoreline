package com.caspian.client.api.handler;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.DisconnectEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.Globals;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 * @author linus
 * @since 1l0
 */
public class TotemHandler implements Globals
{
    //
    private final ConcurrentMap<UUID, Integer> playerTotems =
            new ConcurrentHashMap<>();

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        ClientWorld world = mc.world;
        if (world != null)
        {
            if (event.getPacket() instanceof EntityStatusS2CPacket packet)
            {
                if (packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING)
                {
                    Entity e = packet.getEntity(world);
                    if (e != null && e.isAlive())
                    {
                        playerTotems.put(e.getUuid(),
                                playerTotems.containsKey(e.getUuid()) ?
                                        playerTotems.get(e.getUuid()) + 1 : 1);
                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onDisconnect(DisconnectEvent event)
    {
        playerTotems.clear();
    }

    /**
     * Returns the number of totems popped by the given {@link PlayerEntity}
     * or 0 if the given player has not popped any totems.
     *
     * @return Ehe number of totems popped by the player
     */
    public int getTotems(Entity e)
    {
        return playerTotems.getOrDefault(e.getUuid(), 0);
    }
}
