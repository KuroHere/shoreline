package net.shoreline.client.impl.module.client;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.module.ConcurrentModule;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.util.render.ColorUtil;

import java.awt.*;

/**
 * @author linus
 * @since 1.0
 */
public class ColorsModule extends ConcurrentModule {
    //
    Config<Integer> hueConfig = new NumberConfig<>("Hue", "The saturation of colors", 0, 0, 360);
    Config<Integer> saturationConfig = new NumberConfig<>("Saturation", "The saturation of colors", 0, 50, 100);
    Config<Integer> brightnessConfig = new NumberConfig<>("Brightness", "The brightness of colors", 0, 100, 100);
    Config<Boolean> rainbowConfig = new BooleanConfig("Rainbow", "Renders rainbow colors for modules", false);

    /**
     *
     */
    public ColorsModule() {
        super("Colors", "Client color scheme", ModuleCategory.CLIENT);
    }

    public Color getColor() {
        return ColorUtil.hslToColor(hueConfig.getValue(), saturationConfig.getValue(), brightnessConfig.getValue(), 1.0f);
    }

    public Color getColor(int alpha) {
        return ColorUtil.hslToColor(hueConfig.getValue(), saturationConfig.getValue(), brightnessConfig.getValue(), alpha / 255.0f);
    }

    public Integer getRGB() {
        return getColor().getRGB();
    }

    public int getRGB(int a) {
        return getColor(a).getRGB();
    }
}
