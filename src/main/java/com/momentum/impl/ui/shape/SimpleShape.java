package com.momentum.impl.ui.shape;

import com.momentum.api.util.Globals;

/**
 * Basic {@link IShape} implementation
 *
 * @author linus
 * @since 03/24/2023
 */
public abstract class SimpleShape implements Globals, IShape
{
    // shape positions
    protected float x, y, cx, cy;

    /**
     * Sets the x position
     *
     * @param xv The new x position
     */
    @Override
    public void setX(float xv)
    {
        x = xv;
    }

    /**
     * Gets the x position using
     * {@link org.lwjgl.opengl.GL11#glVertex2f(float, float)} at x and y
     *
     * @return The x position
     */
    @Override
    public float getX()
    {
        return x;
    }

    /**
     * Sets the y position
     *
     * @param yv The new y position
     */
    @Override
    public void setY(float yv)
    {
        x = yv;
    }

    /**
     * Gets the y position
     *
     * @return The y position
     */
    @Override
    public float getY()
    {
        return y;
    }

    /**
     * Gets the center x position
     *
     * @return The center x position
     */
    @Override
    public float getCenterX()
    {
        return cx;
    }

    /**
     * Gets the center y position
     *
     * @return The center y position
     */
    @Override
    public float getCenterY()
    {
        return cy;
    }

    /**
     * Returns whether the x and y fall within the shape's bounds
     *
     * @param x The param x value
     * @param y The param y value
     * @return Falls within bounds
     */
    @Override
    public abstract boolean isWithin(double x, double y);
}
