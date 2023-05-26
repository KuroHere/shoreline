package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.TotemHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TotemManager
{
    //
    private final TotemHandler handler;

    /**
     *
     *
     */
    public TotemManager()
    {
        handler = new TotemHandler();
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    /**
     * Returns the number of totems popped by the given {@link PlayerEntity}
     * or 0 if the given player has not popped any totems.
     *
     * @return Ehe number of totems popped by the player
     */
    public int getTotems(Entity e)
    {
        return handler.getTotems(e);
    }
}
