package com.momentum.impl.ui.click.frame;

import com.momentum.api.module.ModuleCategory;
import com.momentum.impl.ui.shape.Rect;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 03/24/2023
 */
public class Frame extends Rect
{
    // current open tab
    private FrameTab curr;

    // list of tabs in the frame
    private final List<FrameTab> tabs = new ArrayList<>();

    /**
     * Default frame, takes up entire Screen
     */
    public Frame()
    {
        // default dim
        width = 400;
        height = 300;

        // init tabs
        int off = 0;
        for (ModuleCategory c : ModuleCategory.values())
        {
           tabs.add(new FrameTab(this, off, c));
           off += 25;
        }
    }

    /**
     * Draws the frame using
     * {@link org.lwjgl.opengl.GL11#glVertex2f(float, float)} at x and y
     *
     * @param c The color
     */
    @Override
    public void draw(int c)
    {
        // window resolution
        ScaledResolution res = new ScaledResolution(mc);

        // center of screen
        cx = res.getScaledWidth() / 2.0f;
        cy = res.getScaledHeight() / 2.0f;

        // center frame
        x = cx - width / 2.0f;
        y = cy - height / 2.0f;

        // draw rect
        super.draw(c);

        // render tabs section
        if (!tabs.isEmpty())
        {
            // separators
            super.draw(x + (width / 3.0f) - 1, y, x + (width / 3.0f),
                    y + height, 0xff2b2b2b);
            super.draw(x, y + (height / 15.0f) -1, x + width,
                    y + (height / 15.0f), 0xff2b2b2b);

            // draw tabs
            for (FrameTab tab : tabs)
            {
                tab.draw(tab == curr ? 0xe5141414 : 0x00000000);
            }
        }
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
        // check click tabs
        for (FrameTab tab : tabs)
        {
            // mouse within tab
            if (tab.isWithin(mouseX, mouseY))
            {
                // mark current tab
                curr = tab;
                tab.onClick(mouseX, mouseY, mouseButton);
            }
        }
    }
}
