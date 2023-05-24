package com.caspian.client.impl.gui.click.impl.config;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.impl.gui.click.component.Button;
import com.caspian.client.impl.gui.click.component.Frame;
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
    private CategoryButton currTab;

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
        for (ModuleCategory category : ModuleCategory.values()) 
        {
            addButton(new CategoryButton(this, category));
        }
        currTab = (CategoryButton) getButton(0);
    }

    /**
     *
     *
     * @param matrices
     * @param mouseX
     * @param mouseY
     * @param delta
     */
    @Override
    public void render(MatrixStack matrices, float mouseX, float mouseY,
                       float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);
        drawHorizontalLine(matrices, getX(), 80.0, getY() + 13.0,0xff2c2d2f);
        drawVerticalLine(matrices, getX() + 80.0, getY(), getHeight(),
                0xff2c2d2f);
        for (Button tab : getButtons())
        {
            tab.render(matrices, mouseX, mouseY, delta);
        }
    }

    /**
     *
     *
     * @param mouseX
     * @param mouseY
     * @param button
     */
    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        super.mouseClicked(mouseX, mouseY, button);
        for (Button tab : getButtons())
        {
            tab.mouseClicked(mouseX, mouseY, button);
        }
    }

    /**
     *
     *
     * @param mouseX
     * @param mouseY
     * @param button
     */
    @Override
    public void mouseReleased(double mouseX, double mouseY, int button)
    {
        super.mouseReleased(mouseX, mouseY, button);
        for (Button tab : getButtons())
        {
            tab.mouseReleased(mouseX, mouseY, button);
        }
    }

    /**
     *
     *
     * @param keyCode
     * @param scanCode
     * @param modifiers
     */
    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers)
    {
        super.keyPressed(keyCode, scanCode, modifiers);
        for (Button tab : getButtons())
        {
            tab.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    /**
     *
     *
     * @param tab
     */
    public void setCurr(CategoryButton tab)
    {
        currTab = tab;
    }

    /**
     *
     *
     * @param tab
     * @return
     */
    public boolean isCurr(CategoryButton tab) 
    {
        return currTab == tab;
    }
}
