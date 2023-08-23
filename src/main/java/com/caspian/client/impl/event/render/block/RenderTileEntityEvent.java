package com.caspian.client.impl.event.render.block;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

/**
 *
 * @author linus
 * @since 1.0
 */
public class RenderTileEntityEvent extends Event
{
    @Cancelable
    public static class EnchantingTableBook extends RenderTileEntityEvent
    {

    }
}
