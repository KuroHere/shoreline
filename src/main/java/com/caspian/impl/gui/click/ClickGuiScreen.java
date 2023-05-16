package com.caspian.impl.gui.click;

import com.caspian.impl.gui.click.component.Button;
import com.caspian.impl.gui.click.component.Frame;
import com.caspian.impl.gui.click.impl.config.ConfigFrame;
import com.caspian.impl.gui.click.impl.config.ModuleButton;
import com.caspian.impl.gui.click.impl.config.setting.ConfigComponent;
import com.caspian.impl.module.client.ClickGuiModule;
import com.caspian.init.Modules;
import com.caspian.util.Globals;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see ClickGuiModule
 */
public class ClickGuiScreen extends Screen implements Globals
{
    //
    private final List<Frame> frames = new ArrayList<>();

    /**
     *
     */
    public ClickGuiScreen()
    {
        super(Text.literal("ClickGui"));
        Window res = mc.getWindow();
        frames.add(new ConfigFrame(res.getScaledWidth() / 2.0 - 250.0,
                res.getScaledHeight() / 2.0 - 150.0, 500.0, 300.0));
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);
        for (Frame frame : frames)
        {
            frame.render(matrices, mouseX, mouseY, delta);
            float scale = Modules.CLICK_GUI.getConfigValue(
                    "clickgui_scale_config");
            if (scale != 1.0f)
            {
                frame.setDimensions(frame.getWidth() * scale,
                        frame.getHeight() * scale);
                for (Button button : frame.getButtons())
                {
                    button.setDimensions(button.getWidth() * scale,
                            button.getHeight() * scale);
                    if (button instanceof ModuleButton)
                    {
                        for (ConfigComponent<?> component :
                                ((ModuleButton) button).getConfigComponents())
                        {
                            component.setDimensions(component.getWidth() * scale,
                                    component.getHeight() * scale);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     * @return
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        for (Frame frame : frames)
        {
            frame.mouseClicked(mouseX, mouseY, mouseButton);
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     *
     *
     * @param keyCode
     * @param scanCode
     * @param modifiers
     * @return
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        for (Frame frame : frames)
        {
            frame.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     *
     */
    @Override
    public void close()
    {
        super.close();
        Modules.CLICK_GUI.disable();
    }
}
