package net.shoreline.client.impl.event.entity.projectile;

import net.shoreline.client.api.event.Cancelable;
import net.shoreline.client.api.event.Event;

@Cancelable
public class RemoveFireworkEvent extends Event
{
    private final int entityId;

    public RemoveFireworkEvent(int entityId)
    {
        this.entityId = entityId;
    }

    public int getEntityId()
    {
        return entityId;
    }
}
