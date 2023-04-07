package com.momentum.impl.gui.shape;

/**
 * An abstract shape structure
 *
 * @author linus
 * @since 03/24/2023
 */
public interface IShape
{
    /**
     * Returns whether the x and y fall within the shape's bounds
     *
     * @param x The param x value
     * @param y The param y value
     * @return Falls within bounds
     */
    boolean isWithin(double x, double y);

    /**
     * Sets the x position
     *
     * @param x The new x position
     */
    void setX(float x);

    /**
     * Gets the x position
     *
     * @return The x position
     */
    float getX();

    /**
     * Sets the y position
     *
     * @param y The new y position
     */
    void setY(float y);

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