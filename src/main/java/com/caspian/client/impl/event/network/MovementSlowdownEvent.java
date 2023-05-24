package com.caspian.client.impl.event.network;

import com.caspian.client.api.event.Event;
import net.minecraft.client.input.Input;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class MovementSlowdownEvent extends Event
{
    //
    private final Input input;
    
    /**
     *
     *
     * @param input
     */
    public MovementSlowdownEvent(Input input)
    {
        this.input = input;
    }
    
    /**
     *
     *
     * @return
     */
    public Input getInput()
    {
        return input;
    }
}
