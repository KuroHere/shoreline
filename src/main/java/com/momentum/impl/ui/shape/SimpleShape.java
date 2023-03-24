package com.momentum.impl.ui.shape;

/**
 * @author linus
 * @since 03/24/2023
 */
public abstract class SimpleShape implements IShape {

    // shape positions
    protected float x, y, cx, cy;

    /**
     * Gets the x position using
     * {@link org.lwjgl.opengl.GL11#glVertex2f(float, float)} at x and y
     *
     * @return The x position
     */
    @Override
    public float getX() {
        return x;
    }

    /**
     * Gets the y position
     *
     * @return The y position
     */
    @Override
    public float getY() {
        return y;
    }

    /**
     * Gets the center x position
     *
     * @return The center x position
     */
    @Override
    public float getCenterX() {
        return cx;
    }

    /**
     * Gets the center y position
     *
     * @return The center y position
     */
    @Override
    public float getCenterY() {
        return cy;
    }

    /**
     * Draws the shape
     *
     * @param c The color
     */
    @Override
    public abstract void draw(int c);

    /**
     * Returns whether the x and y fall within the shape's bounds
     *
     * @param x The param x value
     * @param y The param y value
     * @return Falls within bounds
     */
    @Override
    public abstract boolean isWithin(float x, float y);
}
