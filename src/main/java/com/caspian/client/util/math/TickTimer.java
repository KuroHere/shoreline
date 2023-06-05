package com.caspian.client.util.math;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.TickEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TickTimer implements Timer
{
    //
    private long ticks;

    /**
     *
     *
     */
    public TickTimer()
    {
        ticks = 0;
        Caspian.EVENT_HANDLER.subscribe(this);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener(priority = 1000)
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            ++ticks;
        }
    }

    /**
     * Returns <tt>true</tt> if the time since the last reset has exceeded
     * the param time.
     *
     * @param time The param time
     * @return <tt>true</tt> if the time since the last reset has exceeded
     * the param time
     */
    @Override
    public boolean passed(Number time)
    {
        return ticks >= time.longValue();
    }

    /**
     *
     */
    @Override
    public void reset()
    {
        ticks = 0;
    }

    /**
     * @return
     */
    @Override
    public long getElapsedTime()
    {
        return ticks;
    }
}
