package com.momentum.impl.ui.click.frame;

import com.momentum.api.module.ModuleCategory;
import com.momentum.impl.ui.shape.Rect;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Tab in a {@link Frame}.
 *
 * @author linus
 * @since 03/24/2023
 */
public class FrameTab extends Rect
{
    // associated category
    private final ModuleCategory category;

    // list of tabs in the frame
    // private final List<?> modules = new ArrayList<>();

    /**
     * Frame with associated {@link ModuleCategory}
     *
     * @param category The module category
     */
    public FrameTab(Frame frame, float off, ModuleCategory category)
    {
        this.x = frame.getX() + 5;
        this.y = frame.getY() + off + 10;
        this.width = frame.getWidth() / 3.0f - 10;
        this.height = 20;
        this.category = category;
    }

    /**
     * Draws the tab using
     * {@link org.lwjgl.opengl.GL11#glVertex2f(float, float)} at x and y
     *
     * @param stack The render stack
     * @param c The color
     */
    @Override
    public void draw(MatrixStack stack, int c)
    {
        // center of tab
        cx = x + width / 2.0f;
        cy = y + height / 2.0f;

        // icon .png img
        // ResourceLocation icon = new ResourceLocation("momentum",
        //        "icon/" + category.name().toLowerCase() + ".png");

        // draw tab
        super.draw(stack, c);
        mc.textRenderer.draw(stack, category.name(), x + 6, y,
                0xffffff);
    }

    /*
     *
    private void drawSvg()
    {
        glPushMatrix();
        glEnable(GL_BLEND);

        glDisable(GL_BLEND);
        glPopMatrix();
    }
     */
}
