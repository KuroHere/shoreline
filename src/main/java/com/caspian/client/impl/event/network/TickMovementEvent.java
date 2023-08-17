package com.caspian.client.impl.event.network;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Cancelable
public class TickMovementEvent extends Event
{
    //
    private int iterations;

    /**
     *
     * @param iterations
     */
    public void setIterations(int iterations)
    {
        this.iterations = iterations;
    }

    /**
     *
     * @return
     */
    public int getIterations()
    {
        return iterations;
    }
}
