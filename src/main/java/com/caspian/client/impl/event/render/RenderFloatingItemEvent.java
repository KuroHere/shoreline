package com.caspian.client.impl.event.render;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 *
 * @author linus
 * @since 1.0
 */
@Cancelable
public class RenderFloatingItemEvent extends Event
{
    private final ItemStack floatingItem;

    public RenderFloatingItemEvent(ItemStack floatingItem)
    {
        this.floatingItem = floatingItem;
    }

    public Item getFloatingItem()
    {
        return floatingItem.getItem();
    }

    public ItemStack getFloatingItemStack()
    {
        return floatingItem;
    }
}
