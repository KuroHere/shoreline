package com.momentum.impl.ui.shape;

/**
 * Shape which can be drawn to the screen
 *
 * @author linus
 * @see 03/24/2023
 */
public interface IDrawable
{
    /**
     * Draws the object
     *
     * @param c The color
     */
    void draw(int c);

    /**
     * Draws the object at the given position
     *
     * @param x The x position
     * @param y The y position
     * @param c The color
     */
    void draw(float x, float y, int c);
}
