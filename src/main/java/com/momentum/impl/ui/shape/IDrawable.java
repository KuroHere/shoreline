package com.momentum.impl.ui.shape;

import net.minecraft.client.util.math.MatrixStack;

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
     * @param stack The render stack
     * @param c The color
     */
    void draw(MatrixStack stack, int c);

    /**
     * Draws the object at the given position
     *
     * @param stack The render stack
     * @param x The x position
     * @param y The y position
     * @param c The color
     */
    void draw(MatrixStack stack, float x, float y, int c);
}
