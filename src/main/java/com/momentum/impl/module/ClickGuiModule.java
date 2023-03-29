package com.momentum.impl.module;

import com.momentum.api.module.ModuleCategory;
import com.momentum.api.module.modules.BindModule;
import com.momentum.impl.ui.click.ClickGuiScreen;
import org.lwjgl.glfw.GLFW;

/**
 *
 */
public class ClickGuiModule extends BindModule
{
    // client clickgui instance
    public static final ClickGuiScreen INSTANCE = new ClickGuiScreen();

    /**
     *
     */
    public ClickGuiModule()
    {
        super("ClickGui", "Opens the clickgui screen", ModuleCategory.CLIENT);
        bind(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        // display client clickgui instance
        mc.setScreen(INSTANCE);
    }
}
