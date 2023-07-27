package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.tick.TickHandler;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see TickHandler
 */
public class TickManager
{
    // The TPS tick handler.
    private final TickHandler handler;

    /**
     *
     *
     */
    public TickManager()
    {
        handler = new TickHandler();
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    /**
     *
     *
     * @return
     */
    public float getTpsAverage()
    {
        float avg = 0.0f;
        if (handler.hasTicks())
        {
            for (float t : handler.getTicks())
            {
                avg += t;
            }
            avg /= handler.size();
        }
        return avg;
    }

    /**
     *
     *
     * @return
     */
    public float getTpsCurrent()
    {
        if (handler.hasTicks())
        {
            return handler.peek();
        }
        return 20.0f;
    }

    /**
     *
     *
     * @return
     */
    public float getTpsMin()
    {
        float min = 20.0f;
        for (float t : handler.getTicks())
        {
            if (t < min)
            {
                min = t;
            }
        }
        return min;
    }
}
