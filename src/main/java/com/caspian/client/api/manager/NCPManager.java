package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.NCPHandler;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NCPManager
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
     * @param time
     * @return
     */
    public boolean passedSinceRubberband(long time)
    {
        return handler.passedSinceRubberband(time);
    }

    /**
     *
     *
     * @param time
     * @param unit
     * @return
     */
    public boolean passedSinceRubberband(long time, TimeUnit unit)
    {
        return handler.passedSinceRubberband(time, unit);
    }

    /**
     *
     *
     * @return
     */
    public long timeSinceLastRubberband()
    {
        return handler.timeSinceLastRubberband();
    }

    /**
     *
     *
     * @return
     */
    public boolean isStuckInRubberband()
    {
        return handler.isStuckInRubberband();
    }
}
