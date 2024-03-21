package net.shoreline.client.api.manager.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.DisconnectEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.util.Globals;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author linus
 * @since 1.0
 */
public class TotemManager implements Globals {
    //
    private final ConcurrentMap<UUID, Integer> totems =
            new ConcurrentHashMap<>();

    /**
     *
     */
    public TotemManager() {
        Shoreline.EVENT_HANDLER.subscribe(this);
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (mc.world != null) {
            if (event.getPacket() instanceof EntityStatusS2CPacket packet
                    && packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING) {
                final Entity entity = packet.getEntity(mc.world);
                if (entity != null && entity.isAlive()) {
                    totems.put(entity.getUuid(), totems.containsKey(entity.getUuid()) ?
                            totems.get(entity.getUuid()) + 1 : 1);
                }
            }
            else if (event.getPacket() instanceof PlayerRemoveS2CPacket packet) {
                for (UUID uuid : packet.profileIds()) {
                    totems.remove(uuid);
                }
            }
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() != EventStage.POST) {
            return;
        }
        for (Entity entity : mc.world.getEntities()) {
            if (!entity.isAlive()) {
                totems.remove(entity.getUuid());
            }
        }
    }

    @EventListener
    public void onDisconnect(DisconnectEvent event) {
        totems.clear();
    }

    /**
     * Returns the number of totems popped by the given {@link PlayerEntity}
     *
     * @param entity
     * @return Ehe number of totems popped by the player
     */
    public int getTotems(Entity entity) {
        return totems.getOrDefault(entity.getUuid(), 0);
    }
}
