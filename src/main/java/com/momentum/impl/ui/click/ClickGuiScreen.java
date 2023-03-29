package com.momentum.impl.ui.click;

import com.momentum.impl.module.ClickGuiModule;
import com.momentum.impl.ui.click.frame.Frame;
import com.momentum.init.Modules;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.io.IOException;

/**
 * ClickGui screen which is displayed during {@link ClickGuiModule#toggle()}
 *
 * @author linus
 * @since 03/25/2023
 */
public class ClickGuiScreen extends Screen
{
    // clickgui frame main
    private final Frame main = new Frame();

    /**
     * Default constructor to create instance of a ClickGui screen
     */
    public ClickGuiScreen()
    {
        super(Text.literal("ClickGui"));
    }

    /**
     * Draws the screen and all components
     *
     * @param mouseX The mouse x
     * @param mouseY The mouse y
     */
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);
    }

    /**
     * Called when the mouse is clicked.
     *
     * @param mouseX The mouse x
     * @param mouseY The mouse y
     * @param mouseButton The clicked button code
     * @throws IOException if button is not valid
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a key is typed (except F11 which toggles fullscreen).
     *
     * @param keyCode The LWJGL keycode
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Called when the screen is unloaded
     */
    @Override
    public void close()
    {
        super.close();
        Modules.CLICKGUI.disable();
    }
}
