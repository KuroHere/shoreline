package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.NCPHandler;
import com.caspian.client.util.math.timer.Timer;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NCPManager implements Timer
{
    //
    private final NCPHandler handler;
    //
    private boolean strict;

    /**
     *
     *
     */
    public NCPManager()
    {
        handler = new NCPHandler();
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    /**
     *
     *
     * @param strict
     */
    public void setStrict(boolean strict)
    {
        this.strict = strict;
    }

    /**
     *
     * @return
     */
    public boolean isStrict()
    {
        return strict;
    }

    /**
     *
     *
     * @return
     */
    public boolean isInRubberband()
    {
        return handler.isInRubberband();
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
        return handler.passedSinceRubberband(time.longValue());
    }

    /**
     * Resets the current elapsed time state of the timer and restarts the
     * timer from 0.
     */
    @Deprecated
    @Override
    public void reset()
    {
        // DEPRECATED
    }

    /**
     * Returns the elapsed time since the last reset of the timer.
     *
     * @return The elapsed time since the last reset
     */
    @Override
    public long getElapsedTime()
    {
        return handler.timeSinceRubberband();
    }

    /**
     *
     *
     * @param time
     */
    @Deprecated
    @Override
    public void setElapsedTime(Number time)
    {
        // DEPRECATED
    }
}
