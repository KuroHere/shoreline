package com.caspian.client.impl.event.render.entity;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;
import net.minecraft.entity.LivingEntity;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Cancelable
public class RenderArmorEvent extends Event
{
    private final LivingEntity entity;

    public RenderArmorEvent(LivingEntity entity)
    {
        this.entity = entity;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }
}
