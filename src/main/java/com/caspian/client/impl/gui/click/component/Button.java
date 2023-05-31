package com.caspian.client.impl.gui.click.component;

/**
 *
 * @author linus
 * @since 1.0
 */
public abstract class Button extends Component implements Interactable
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
     * @param mouseX
     * @param mouseY
     * @param button
     */
    @Override
    public abstract void mouseClicked(double mouseX, double mouseY, int button);

    /**
     *
     *
     * @param mouseX
     * @param mouseY
     * @param button
     */
    @Override
    public abstract void mouseReleased(double mouseX, double mouseY, int button);

    /**
     *
     *
     * @param keyCode
     * @param scanCode
     * @param modifiers
     */
    @Override
    public abstract void keyPressed(int keyCode, int scanCode, int modifiers);

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
