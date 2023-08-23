package com.caspian.client.impl.event.render.entity;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;
import net.minecraft.entity.ItemEntity;

/**
 *
 * @author linus
 * @since 1.0
 */
@Cancelable
public class RenderItemEvent extends Event
{
    private final ItemEntity itemEntity;

    public RenderItemEvent(ItemEntity itemEntity)
    {
        this.itemEntity = itemEntity;
    }

    public ItemEntity getItem()
    {
        return itemEntity;
    }
}
