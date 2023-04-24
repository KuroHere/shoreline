package com.caspian.impl.gui.click.component;

import com.caspian.util.Globals;
import net.minecraft.client.util.math.MatrixStack;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class Frame extends Component implements Globals
{
    /**
     *
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public Frame(double x, double y, double width, double height)
    {
        setPos(x, y);
        setWidth(width);
        setHeight(height);
    }

    /**
     *
     *
     * @param matrices
     * @param mouseX
     * @param mouseY
     * @param delta
     */
    @Override
    public void render(MatrixStack matrices, float mouseX, float mouseY,
                       float delta)
    {
        drawRoundedRect(matrices, getX(), getY(), getWidth(), getHeight(),
                0xe5000000);
        drawCircle(matrices, getX() + 4.0, getY() + 5.0, 2,
                0xffff294d);
        drawCircle(matrices, getX() + 8.0, getY() + 5.0, 2,
                0xed60ab6b);
    }

    /**
     *
     *
     * @param mouseX
     * @param mouseY
     * @param button
     */
    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isMouseOver(mouseX, mouseY, getX() + 2.0, getY() + 3.0, 4.0, 4.0))
        {
            closeFrame();
        }

        if (isMouseOver(mouseX, mouseY, getX() + 6.0, getY() + 3.0, 4.0, 4.0))
        {
            maximizeFrame();
        }
    }

    /**
     *
     *
     * @param mouseX
     * @param mouseY
     * @param button
     */
    @Override
    public void mouseReleased(double mouseX, double mouseY, int button)
    {

    }

    /**
     *
     *
     * @param keyCode
     * @param scanCode
     * @param modifiers
     */
    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers)
    {

    }

    /**
     *
     */
    public void closeFrame()
    {

    }

    /**
     *
     */
    public void maximizeFrame()
    {

    }
}
