package com.caspian.client.impl.module.client;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.config.setting.NumberDisplay;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.impl.gui.click.ClickGuiScreen;
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
    private static ClickGuiScreen CLICK_GUI_SCREEN;

    //
    Config<Float> scaleConfig = new NumberConfig<>("Scale", "ClickGui " +
            "component scaling factor.", 0.1f, 1.0f, 3.0f,
            NumberDisplay.PERCENT);

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
        // if the player is null or we're not in a world, turn off module and return
        if (mc.player == null || mc.world == null)
        {
            toggle();
            return;
        }

        // initialize the null gui screen instance
        if (CLICK_GUI_SCREEN == null)
        {
            CLICK_GUI_SCREEN = new ClickGuiScreen();
        }

        mc.setScreen(CLICK_GUI_SCREEN);
    }

    /**
     *
     *
     * @return
     */
    public Float getScale()
    {
        return scaleConfig.getValue();
    }
}
