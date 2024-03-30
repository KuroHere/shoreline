package net.shoreline.client.impl.module.client;

import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.gui.click.ClickGuiScreen;
import net.shoreline.client.impl.gui.click2.ClientGuiSecondaryScreen;
import org.lwjgl.glfw.GLFW;

/**
 * @author linus
 * @see ClickGuiScreen
 * @since 1.0
 */
public class ClickGuiModule extends ToggleModule {
    //
    private static ClickGuiScreen CLICK_GUI_SCREEN;
    // private static ClientGuiSecondaryScreen SECONDARY_CLICK_GUI_SCREEN;

    // TODO: Fix Gui scaling
    public float scaleConfig = 1.0f;

    /**
     *
     */
    public ClickGuiModule() {
        super("ClickGui", "Opens the clickgui screen", ModuleCategory.CLIENT,
                GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    /**
     *
     */
    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            toggle();
            return;
        }
        // initialize the null gui screen instance
        if (CLICK_GUI_SCREEN == null) {
            CLICK_GUI_SCREEN = new ClickGuiScreen(this);
        }
        mc.setScreen(CLICK_GUI_SCREEN);

//        if (SECONDARY_CLICK_GUI_SCREEN == null) {
//            SECONDARY_CLICK_GUI_SCREEN = new ClientGuiSecondaryScreen(this);
//        }
//        mc.setScreen(SECONDARY_CLICK_GUI_SCREEN);
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) {
            toggle();
            return;
        }
        mc.player.closeScreen();
    }

    /**
     * @return
     */
    public Float getScale() {
        return scaleConfig;
    }
}
