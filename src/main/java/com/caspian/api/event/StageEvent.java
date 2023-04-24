package com.caspian.api.event;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Event
 * @see EventStage
 */
public class StageEvent extends Event
{
    // The current event stage which determines which segment of the event is
    // currently running.
    private EventStage stage;

    /**
     *
     *
     * @param stg
     */
    public void setStage(EventStage stg)
    {
        stage = stg;
    }

    /**
     * Returns the current {@link EventStage} of the {@link Event}.
     *
     * @return The current event stage
     */
    public EventStage getStage()
    {
        return stage;
    }
}
