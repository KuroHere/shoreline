package com.caspian.client.impl.gui.click.impl.config.setting;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.gui.click.impl.config.CategoryFrame;
import com.caspian.client.init.Modules;
import com.caspian.client.util.string.EnumFormatter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 */
public class DropdownButton extends ConfigButton<Enum<?>>
{
    // Current enum value index
    private int index;

    /**
     *
     *
     * @param frame
     * @param config
     */
    public DropdownButton(CategoryFrame frame, Config<Enum<?>> config, float x, float y)
    {
        super(frame, config, x, y);
    }

    /**
     *
     *
     * @param matrices
     * @param ix
     * @param iy
     * @param mouseX
     * @param mouseY
     * @param delta
     */
    @Override
    public void render(MatrixStack matrices, float ix, float iy, float mouseX,
                       float mouseY, float delta)
    {
        x = ix;
        y = iy;
        String val = EnumFormatter.formatEnum(config.getValue());
        rect(matrices, Modules.COLORS.getRGB());
        RenderManager.renderText(matrices, config.getName() + Formatting.GRAY +
                        " " + val, ix + 2.0f, iy + 4.0f, -1);
    }

    /**
     *
     *
     * @param mouseX
     * @param mouseY
     * @param button
     */
    @SuppressWarnings("unchecked")
    @Override
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isWithin(mouseX, mouseY))
        {
            Enum<?> val = config.getValue();
            String[] values = Arrays.stream(val.getClass().getEnumConstants())
                    .map(Enum::name)
                    .toArray(String[]::new);
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                index = index + 1 > values.length - 1 ? 0 : index + 1;
                config.setValue(Enum.valueOf(val.getClass(), values[index]));
            }
            else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            {
                index = index - 1 < 0 ? values.length - 1 : index - 1;
                config.setValue(Enum.valueOf(val.getClass(), values[index]));
            }
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

    }
}
