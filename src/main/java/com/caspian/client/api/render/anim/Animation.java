package com.caspian.client.api.render.anim;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class Animation
{
    //
    private final Easing easing;
    private final long time;

    /**
     *
     *
     * @param easing
     * @param time
     */
    public Animation(Easing easing, long time)
    {
        this.easing = easing;
        this.time = time;
    }

    /**
     *
     *
     * @param easing
     */
    public Animation(Easing easing)
    {
        this(easing, 300);
    }

    /**
     *
     */
    public void resetStateHard()
    {

    }
}
