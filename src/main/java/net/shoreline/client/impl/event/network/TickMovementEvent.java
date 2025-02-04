package net.shoreline.client.impl.event.network;

import net.shoreline.client.api.event.Cancelable;
import net.shoreline.client.api.event.Event;

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
