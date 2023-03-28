package com.momentum.impl.ui.shape;

import org.lwjgl.opengl.GL11;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * Drawable rectangle which can be drawn to the screen by calling
 * {@link IDrawable#draw(int)} and interacted with through
 * {@link IClickable#onClick(int, int, int)} and
 * {@link IClickable#onType(char, int)} methods
 *
 * @author linus
 * @since 03/24/2023
 *
 * @see com.momentum.impl.ui.click.frame.Frame
 * @see com.momentum.impl.ui.click.frame.FrameTab
 */
public class Rect extends SimpleShape implements IDrawable, IClickable
{
    // rectangle width and height
    protected float width, height;

    /**
     * Draws the shape using
     * {@link org.lwjgl.opengl.GL11#glVertex2f(float, float)} at x and y
     *
     * @param c The color
     */
    @Override
    public void draw(int c)
    {
        // draw rect
        draw(x, y, width, height, c);
    }

    /**
     * Draws the shape using
     * {@link GL11#glVertex2f(float, float)} at the
     * parameter x and y
     *
     * @param x The x position
     * @param y The y position
     * @param c The color
     */
    @Override
    public void draw(float x, float y, int c)
    {
        // draw rect
        draw(x, y, width, height, c);
    }

    /**
     * Draws the rect using
     * {@link GL11#glVertex2f(float, float)} at the
     * parameter x and y with size parameter width and height
     *
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param c The color
     */
    public void draw(float x, float y, float width, float height, int c)
    {
        // color
        Color color = new Color(c, true);

        // gl args
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);
        glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f,
                color.getBlue() / 255.0f, color.getAlpha() / 255.0f);

        // assign vertices
        glVertex2f(x, y);
        glVertex2f(x, y + height);
        glVertex2f(x + width, y + height);
        glVertex2f(x + width, y);
        glColor4f(0.0f, 0.0f, 0.0f, 1.0f);

        // end
        glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    /**
     * Sets the rect size
     *
     * @param w The new rect width
     * @param h The new rect height
     */
    public void setSize(float w, float h)
    {
        width = w;
        height = h;
    }

    /**
     * Returns the rectangle width
     *
     * @return The rectangle width
     */
    public float getWidth()
    {
        return width;
    }

    /**
     * Returns the rectangle height
     *
     * @return The rectangle height
     */
    public float getHeight()
    {
        return height;
    }

    /**
     * Called when the mouse is clicked.
     *
     * @param mouseX The mouse x
     * @param mouseY The mouse y
     * @param mouseButton The clicked button code
     */
    @Override
    public void onClick(int mouseX, int mouseY, int mouseButton)
    {
        // impl
    }

    /**
     * Called when a key is typed (except F11 which toggles fullscreen).
     *
     * @param typedChar The keyboard character
     * @param keyCode   The LWJGL keycode
     */
    @Override
    public void onType(char typedChar, int keyCode)
    {
        // impl
    }

    /**
     * Returns whether the x and y fall within the shape's bounds
     *
     * @param xv The param x value
     * @param yv The param y value
     * @return Falls within bounds
     */
    @Override
    public boolean isWithin(float xv, float yv)
    {
        // call helper
        return isWithin(xv, yv, x, y, width, height);
    }

    /**
     * Returns <tt>true</tt> if a given value is between the min and the max
     *
     * @param xval The x position of the value
     * @param yval The y position of the value
     * @param xmin The x position of the minimum bound
     * @param xmax The x position of the maximum bound
     * @param ymin The y position of the minimum bound
     * @param ymax The y position of the maximum bound
     * @return Whether the given value is between the min and the mix
     */
    public boolean isWithin(float xval, float yval, float xmin, float ymin,
                            float xmax, float ymax)
    {
        return xval >= xmin
                && xval <= xmin + xmax
                && yval >= ymin
                && yval <= ymin + ymax;
    }
}
