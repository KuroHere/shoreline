package net.shoreline.client.impl.event.world;

import net.shoreline.client.api.event.Event;
import net.shoreline.client.util.Globals;
import net.minecraft.entity.Entity;

public class RemoveEntityEvent extends Event implements Globals
{
    private final Entity entity;
    private final Entity.RemovalReason removalReason;

    public RemoveEntityEvent(Entity entity, Entity.RemovalReason removalReason)
    {
        this.entity = entity;
        this.removalReason = removalReason;
    }

    /**
     *
     * @return
     */
    public Entity getEntity()
    {
        return entity;
    }

    public Entity.RemovalReason getRemovalReason()
    {
        return removalReason;
    }
}
