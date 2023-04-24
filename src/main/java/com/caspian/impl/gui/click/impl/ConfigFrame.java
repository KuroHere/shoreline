package com.caspian.impl.gui.click.impl;

import com.caspian.api.config.Config;
import com.caspian.api.module.ModuleCategory;
import com.caspian.impl.gui.click.component.Component;
import com.caspian.impl.gui.click.component.Frame;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Configuration {@link Frame} (commonly referred to as the "ClickGui") which
 * allows the user to configure a {@link Module}'s {@link Config} values.
 *
 * @author linus
 * @since 1.0
 *
 * @see Frame
 * @see Module
 * @see Config
 */
public class ConfigFrame extends Frame
{
    //
    private ConfigFrameTab frameTab;

    /**
     *
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public ConfigFrame(double x, double y, double width, double height)
    {
        super(x, y, width, height);

    }

    @Override
    public void render(MatrixStack matrices, float mouseX, float mouseY,
                       float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);
        drawHorizontalLine(matrices, getX(), 80, getY() + 13, 0xff2c2d2f);
        drawVerticalLine(matrices, getX() + 80, getY(), getHeight(), 0xff2c2d2f);
    }

    public static class ConfigFrameTab extends Component
    {
        //
        public boolean curr;

        //
        private final ModuleCategory category;

        /**
         *
         *
         * @param category
         */
        public ConfigFrameTab(ModuleCategory category)
        {
            this.category = category;
        }

        /**
         * @param matrices
         * @param mouseX
         * @param mouseY
         * @param delta
         */
        @Override
        public void render(MatrixStack matrices, float mouseX, float mouseY,
                           float delta)
        {
            if (isCurr())
            {
                fill(matrices, getX(), getY(), getWidth(), getHeight(),
                        0x872c2e30);
            }

            // mc.getTextureManager().bindTexture();
            // drawTexture(matrices, getX(), getY(), 0, 0, 6, 6);
            drawTextWithShadow(matrices, mc.textRenderer, category.name(),
                    getX() + 6, getY(), 0xffffff);
        }

        /**
         * @param mouseX
         * @param mouseY
         * @param button
         */
        @Override
        public void mouseClicked(double mouseX, double mouseY, int button) {

        }

        /**
         * @param mouseX
         * @param mouseY
         * @param button
         */
        @Override
        public void mouseReleased(double mouseX, double mouseY, int button) {

        }

        /**
         * @param keyCode
         * @param scanCode
         * @param modifiers
         */
        @Override
        public void keyPressed(int keyCode, int scanCode, int modifiers) {

        }

        public boolean isCurr()
        {
            return curr;
        }

        public void setCurr(boolean curr)
        {
            this.curr = curr;
        }
    }
}
