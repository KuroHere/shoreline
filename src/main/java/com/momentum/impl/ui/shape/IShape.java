package com.momentum.impl.ui.shape;

/**
 * @author linus
 * @since 03/24/2023
 */
public interface IShape {

    /**
     * Draws the shape using
     * {@link org.lwjgl.opengl.GL11#glVertex2f(float, float)} at x and y
     *
     * @param c The color
     */
    void draw(int c);

    /**
     * Returns whether the x and y fall within the shape's bounds
     *
     * @param x The param x value
     * @param y The param y value
     * @return Falls within bounds
     */
    boolean isWithin(float x, float y);

    /**
     * Gets the x position
     *
     * @return The x position
     */
    float getX();

    /**
     * Gets the y position
     *
     * @return The y position
     */
    float getY();

    /**
     * Gets the center x position
     *
     * @return The center x position
     */
    float getCenterX();

    /**
     * Gets the center y position
     *
     * @return The center y position
     */
    float getCenterY();
}