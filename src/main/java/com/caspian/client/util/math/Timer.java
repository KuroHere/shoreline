package com.caspian.client.util.math;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public interface Timer
{
    /**
     * Returns <tt>true</tt> if the time since the last reset has exceeded
     * the param time.
     *
     * @param time The param time
     * @return <tt>true</tt> if the time since the last reset has exceeded
     * the param time
     */
    boolean passed(Number time);

    /**
     *
     */
    void reset();

    /**
     *
     * @return
     */
    long getElapsedTime();
}
