package com.momentum.impl.module.client;

import com.momentum.api.module.ModuleCategory;
import com.momentum.api.module.modules.BindModule;
import com.momentum.impl.ui.click.ClickGuiScreen;
import org.lwjgl.glfw.GLFW;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ClickGuiModule extends BindModule
{
    // client clickgui instance
    public static final ClickGuiScreen SCREEN_INSTANCE = new ClickGuiScreen();

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
        // display client clickgui instance
        mc.setScreen(SCREEN_INSTANCE);
    }
}
