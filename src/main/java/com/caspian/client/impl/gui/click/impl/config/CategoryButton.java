package com.caspian.client.impl.gui.click.impl.config;

import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.impl.gui.click.component.Button;
import com.caspian.client.init.Managers;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 */
public class CategoryButton extends Button
{
    //
    private final ModuleCategory category;
    //
    private final List<ModuleButton> moduleButtons = new ArrayList<>();

    /**
     *
     *
     * @param frame
     * @param category
     */
    public CategoryButton(ConfigFrame frame, ModuleCategory category)
    {
        super(frame);
        this.category = category;
        for (Module module : Managers.MODULE.getModules())
        {
            if (module.getCategory() == category)
            {
                moduleButtons.add(new ModuleButton(frame, module));
            }
        }
        setDimensions(60.0, 18.0);
    }

    /**
     *
     *
     * @return
     */
    public List<ModuleButton> getModuleButtons()
    {
        return moduleButtons;
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
        setPos(getFrame().getX() + 10.0, getFrame().getY() + 45.0 );
        if (((ConfigFrame) getFrame()).isCurr(this))
        {
            drawRoundedRect(matrices, getX(), getY(), getWidth(), getHeight(),
                    0x872c2e30);
        }
        // mc.getTextureManager().bindTexture();
        // drawTexture(matrices, getX(), getY());
        drawTextWithShadow(matrices, mc.textRenderer, category.name(),
                getX() + 6.0, getY(), 0xffffff);
        for (ModuleButton moduleButton : moduleButtons)
        {
            moduleButton.render(matrices, mouseX, mouseY, delta);
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
        if (isMouseOver(mouseX, mouseY, getX(), getY(), getWidth(),
                getHeight()))
        {
            ((ConfigFrame) getFrame()).setCurr(this);
        }
        for (ModuleButton moduleButton : moduleButtons)
        {
            moduleButton.mouseClicked(mouseX, mouseY, button);
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
        for (ModuleButton moduleButton : moduleButtons)
        {
            moduleButton.mouseReleased(mouseX, mouseY, button);
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
        for (ModuleButton moduleButton : moduleButtons)
        {
            moduleButton.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
