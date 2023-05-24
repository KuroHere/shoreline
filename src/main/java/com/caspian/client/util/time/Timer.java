package com.caspian.client.util.time;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class Timer
{
    // The cached time since last reset which indicates the time passed since
    // the last timer reset
    private long time;

    /**
     * Default constructor which will initialize the time to the current time
     * which means {@link #passed(Number)} and {@link #passed(Number, TimeUnit)}
     * will always return <tt>true</tt> initially
     */
    public Timer()
    {
        this.time = System.currentTimeMillis();
    }

    /**
     * Returns <tt>true</tt> if the time since the last reset has exceeded
     * the param time.
     *
     * @param time The param time
     * @return <tt>true</tt> if the time since the last reset has exceeded
     * the param time
     */
    public boolean passed(Number time)
    {
        return System.currentTimeMillis() - this.time > (Long) time;
    }

    /**
     * Returns <tt>true</tt> if the time since the last reset has exceeded
     * the param time which is in the param units.
     *
     * @param time The param time
     * @param unit The unit of the time
     * @return <tt>true</tt> if the time since the last reset has exceeded
     * the param time
     *
     * @see #passed(Number)
     */
    public boolean passed(Number time, TimeUnit unit)
    {
        return passed(unit.toMillis((Long) time));
    }

    /**
     * Sets the cached time since the last reset to the current time
     */
    public void reset()
    {
        time = System.currentTimeMillis();
    }
}
