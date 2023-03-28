package com.momentum.impl.module;

import com.momentum.api.module.ModuleCategory;
import com.momentum.api.module.modules.BindModule;
import com.momentum.impl.ui.click.ClickGuiScreen;
import org.lwjgl.input.Keyboard;

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
        bind(Keyboard.KEY_RSHIFT);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        // display client clickgui instance
        mc.addScheduledTask(() -> mc.displayGuiScreen(INSTANCE));
    }
}
