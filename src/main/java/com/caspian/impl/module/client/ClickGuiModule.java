package com.caspian.impl.module.client;

import com.caspian.api.module.ToggleModule;
import com.caspian.api.module.ModuleCategory;
import com.caspian.impl.gui.click.ClickGuiScreen;
import org.lwjgl.glfw.GLFW;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see ClickGuiScreen
 */
public class ClickGuiModule extends ToggleModule
{
    //
    private static final ClickGuiScreen CLICK_GUI_SCREEN = new ClickGuiScreen();

    /**
     *
     */
    public ClickGuiModule()
    {
        super("ClickGui", "Opens the clickgui screen", ModuleCategory.CLIENT,
                GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        mc.setScreen(CLICK_GUI_SCREEN);
    }
}
