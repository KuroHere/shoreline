package com.caspian.client.impl.module.client;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.ColorConfig;
import com.caspian.client.api.module.ConcurrentModule;
import com.caspian.client.api.module.ModuleCategory;

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
    final Config<Color> colorConfig = new ColorConfig("Color", "Global" +
            " client color", new Color(0xffff0050));
    /**
     *
     */
    public ColorsModule()
    {
        super("Colors", "Client color scheme", ModuleCategory.CLIENT);
    }

    /**
     *
     *
     * @return
     */
    public Color getColor()
    {
        return colorConfig.getValue();
    }

    /**
     *
     *
     * @param a The alpha mask
     * @return
     *
     * @see #getColor()
     */
    public Color getColor(int a)
    {
        int r = ((ColorConfig) colorConfig).getRed();
        int g = ((ColorConfig) colorConfig).getGreen();
        int b = ((ColorConfig) colorConfig).getBlue();
        return new Color(r, g, b, a);
    }

    /**
     *
     *
     * @return
     */
    public Integer getRGB()
    {
        return getColor().getRGB();
    }

    /**
     *
     *
     * @param a The alpha mask
     * @return
     */
    public int getRGB(int a)
    {
        return getColor(a).getRGB();
    }
}
