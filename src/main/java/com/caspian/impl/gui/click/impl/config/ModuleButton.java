package com.caspian.impl.gui.click.impl.config;

import com.caspian.api.config.Config;
import com.caspian.api.module.Module;
import com.caspian.impl.gui.click.component.Button;
import com.caspian.impl.gui.click.impl.config.setting.*;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Module
 * @see ConfigFrame
 */
public class ModuleButton extends Button
{
    //
    private final Module module;

    //
    private final List<ConfigComponent<?>> configComponents = new ArrayList<>();

    /**
     *
     *
     * @param frame
     */
    @SuppressWarnings("unchecked")
    public ModuleButton(ConfigFrame frame, Module module)
    {
        super(frame);
        this.module = module;
        for (Config<?> config : module.getConfigs())
        {
            if (config.getValue() instanceof Boolean)
            {
                configComponents.add(new SwitchComponent(frame,
                        (Config<Boolean>) config));
            }
            else if (config.getValue() instanceof Double)
            {
                configComponents.add(new SliderComponent<>(frame,
                        (Config<Double>) config));
            }
            else if (config.getValue() instanceof Float)
            {
                configComponents.add(new SliderComponent<>(frame,
                        (Config<Float>) config));
            }
            else if (config.getValue() instanceof Integer)
            {
                configComponents.add(new SliderComponent<>(frame,
                        (Config<Integer>) config));
            }
            else if (config.getValue() instanceof Enum<?>)
            {
                configComponents.add(new DropdownComponent(frame,
                        (Config<Enum<?>>) config));
            }
            else if (config.getValue() instanceof String)
            {
                configComponents.add(new TextComponent(frame,
                        (Config<String>) config));
            }
        }
    }

    /**
     *
     *
     * @return
     */
    public Module getModule()
    {
        return module;
    }

    /**
     *
     *
     * @return
     */
    public List<ConfigComponent<?>> getConfigComponents()
    {
        return configComponents;
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
    public void render(MatrixStack matrices, float mouseX, float mouseY,
                       float delta)
    {
        for (ConfigComponent<?> component : configComponents)
        {
            component.render(matrices, mouseX, mouseY, delta);
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
    public void mouseClicked(double mouseX, double mouseY, int button)
    {
        for (ConfigComponent<?> component : configComponents)
        {
            component.mouseClicked(mouseX, mouseY, button);
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
        for (ConfigComponent<?> component : configComponents)
        {
            component.mouseReleased(mouseX, mouseY, button);
        }
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
        for (ConfigComponent<?> component : configComponents)
        {
            component.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
