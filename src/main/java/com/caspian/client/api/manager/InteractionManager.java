package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.InteractionHandler;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class InteractionManager
{
    //
    private final InteractionHandler handler;

    /**
     *
     *
     */
    public InteractionManager()
    {
        handler = new InteractionHandler();
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    /**
     *
     *
     * @return
     */
    public boolean isBreakingBlock()
    {
        return handler.isBreakingBlock();
    }
}
