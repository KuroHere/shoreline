package com.momentum.impl.ui.click;

import com.momentum.impl.module.ClickGuiModule;
import com.momentum.impl.ui.click.frame.Frame;
import com.momentum.init.Modules;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/**
 * ClickGui screen which is displayed during {@link ClickGuiModule#toggle()}
 *
 * @author linus
 * @since 03/25/2023
 */
public class ClickGuiScreen extends GuiScreen
{
    // clickgui frame main
    private final Frame main = new Frame();

    /**
     * Draws the screen and all components
     *
     * @param mouseX The mouse x
     * @param mouseY The mouse y
     * @param partialTicks The render partial ticks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        main.draw(0xe5000000);
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
            throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        main.onClick(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a key is typed (except F11 which toggles fullscreen).
     *
     * @param typedChar The keyboard character
     * @param keyCode The LWJGL keycode
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        main.onType(typedChar, keyCode);
    }

    /**
     * Called when the screen is unloaded
     */
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Modules.CLICKGUI.disable();
    }
}
