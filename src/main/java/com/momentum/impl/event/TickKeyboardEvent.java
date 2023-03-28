package com.momentum.impl.event;

import com.momentum.api.event.Event;

/**
 * Runs during {@link net.minecraft.client.Minecraft#runTickKeyboard()}
 *
 * @author linus
 * @since 03/26/2023
 */
public class TickKeyboardEvent extends Event
{
    /**
     * Gets this event's cancelable state
     *
     * @return The cancelable state
     */
    @Override
    public boolean isCancelable()
    {
        return false;
    }
}
