package com.caspian.client.api.handler.rotation;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public enum RotationPriority
{
    HIGHEST(1000),
    HIGH(750),
    NORMAL(500),
    LOW(100);

    //
    private final int priority;

    /**
     *
     *
     * @param priority
     */
    RotationPriority(int priority)
    {
        this.priority = priority;
    }

    public int getPriority()
    {
        return priority;
    }
}
