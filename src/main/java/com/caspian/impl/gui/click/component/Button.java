package com.caspian.impl.gui.click.component;

/**
 *
 * @author linus
 * @since 1.0
 */
public abstract class Button extends Component
{
    //
    private final Frame frame;

    /**
     *
     *
     * @param frame
     */
    public Button(Frame frame)
    {
        this.frame = frame;
    }

    /**
     *
     *
     * @return
     */
    public Frame getFrame()
    {
        return frame;
    }
}
