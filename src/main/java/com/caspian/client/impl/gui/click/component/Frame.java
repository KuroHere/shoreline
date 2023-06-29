package com.caspian.client.impl.gui.click.component;

import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class Frame extends Component implements Interactable
{
    //
    private final List<Button> buttons = new ArrayList<>();
    //
    private double prevX, prevY, prevWidth, prevHeight;
    private boolean maximized;

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
     * @return
     */
    public List<Button> getButtons()
    {
        return buttons;
    }

    /**
     *
     *
     * @param idx
     * @return
     */
    public Button getButton(int idx)
    {
        return buttons.get(idx);
    }

    /**
     *
     *
     * @param button
     */
    public void addButton(Button button)
    {
        buttons.add(button);
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
                maximized ? 0xfffcbc3c : 0xff60ab6b);
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
            if (maximized)
            {
                minimizeFrame();
            }
            else
            {
                maximizeFrame();
            }
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
        prevX = getX();
        prevY = getY();
        prevWidth = getWidth();
        prevHeight = getHeight();
        setPos(0, 0);
        Window window = mc.getWindow();
        setDimensions(window.getScaledWidth(), window.getScaledHeight());
        maximized = true;
    }

    /**
     *
     */
    public void minimizeFrame()
    {
        setPos(prevX, prevY);
        setDimensions(prevWidth, prevHeight);
        maximized = false;
    }
}
