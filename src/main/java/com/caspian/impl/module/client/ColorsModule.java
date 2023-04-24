package com.caspian.impl.module.client;

import com.caspian.api.config.Config;
import com.caspian.api.config.setting.ColorConfig;
import com.caspian.api.module.ConcurrentModule;
import com.caspian.api.module.ModuleCategory;

import java.awt.*;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ColorsModule extends ConcurrentModule
{
    //
    public final Config<Color> colorConfig = new ColorConfig("Color", "Global" +
            " client color", new Color(255, 0, 80));
    /**
     *
     */
    public ColorsModule()
    {
        super("Colors", "Client color scheme", ModuleCategory.CLIENT);
    }

    public Color getColor()
    {
        return colorConfig.getValue();
    }

    /**
     *
     *
     * @param alpha
     * @return
     *
     * @see #getColor()
     */
    public Color getColor(int alpha)
    {
        Color c = getColor();
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public Integer getColorRGB()
    {
        return getColor().getRGB();
    }

    public int getColorRGB(int alpha)
    {
        return getColor(alpha).getRGB();
    }
}
