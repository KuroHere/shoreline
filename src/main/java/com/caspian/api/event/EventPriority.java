package com.caspian.api.event;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Event
 */
public enum EventPriority
{
    /**
     *
     */
    HIGH(1),

    /**
     *
     */
    NORMAL(0),

    /**
     *
     */
    LOW(-1);

    private final int priority;

    EventPriority(int priority)
    {
        this.priority = priority;
    }

    /**
     *
     *
     * @return
     */
    public int getPriority()
    {
        return priority;
    }
}
