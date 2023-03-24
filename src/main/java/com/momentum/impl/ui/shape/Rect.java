package com.momentum.impl.ui.shape;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author linus
 * @since 03/24/2023
 */
public class Rect extends SimpleShape {

    /**
     * Draws the shape using
     * {@link org.lwjgl.opengl.GL11#glVertex2f(float, float)} at x and y
     *
     * @param c The color
     */
    @Override
    public void draw(int c) {

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

    // rectangle width and height
    float width, height;

    /**
     * Returns whether the x and y fall within the shape's bounds
     *
     * @param xv The param x value
     * @param yv The param y value
     * @return Falls within bounds
     */
    @Override
    public boolean isWithin(float xv, float yv) {

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
                            float xmax, float ymax) {
        return xval >= xmin
                && xval <= xmin + xmax
                && yval >= ymin
                && yval <= ymin + ymax;
    }
}
