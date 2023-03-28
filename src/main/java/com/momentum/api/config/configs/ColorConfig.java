package com.momentum.api.config.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.momentum.api.config.Config;
import com.momentum.api.config.file.ConfigFile;
import com.momentum.impl.ui.click.ClickGuiScreen;

import java.awt.Color;

/**
 * Color configuration associated with {@link } in
 * {@link ClickGuiScreen}.
 *
 * @author linus
 * @since 03/23/2023
 */
public class ColorConfig extends Config<Color>
{
    // rgb value of color
    // equivalent to {@link Color#getRGB()} of value
    private int rgb;

    /**
     * Config default constructor
     *
     * @param name  The config name
     * @param desc  The config description
     * @param value The config value
     */
    public ColorConfig(String name, String desc, Color value)
    {
        super(name, desc, value);
        this.rgb = value.getRGB();
    }

    /**
     * ColorConfig alternate constructor with hex value
     *
     * @param name  The config name
     * @param desc  The config description
     * @param rgb The config rgb value
     */
    public ColorConfig(String name, String desc, Integer rgb)
    {
        this(name, desc,
                new Color(rgb, (rgb & 0xff000000) != 0xff000000));
    }

    /**
     * Sets the color value
     *
     * @param val The new color value
     */
    @Override
    public void setValue(Color val)
    {
        // update rgb value
        super.setValue(val);
        rgb = val.getRGB();
    }

    /**
     * Sets the color rgb value
     *
     * @param rgb The new color rgb value
     */
    public void setValue(int rgb)
    {
        // update rgb value
        setValue(new Color(rgb, (rgb & 0xff000000) != 0xff000000));
        this.rgb = rgb;
    }

    /**
     * Gets the rgb value
     *
     * @return The rgb value
     */
    public int getRgb()
    {
        return rgb;
    }

    /**
     * Returns the red component in the range 0-255
     *
     * @return The red component
     */
    public int getRed()
    {
        return (rgb >> 16) & 0xff;
    }

    /**
     * Returns the green component in the range 0-255
     *
     * @return The green component
     */
    public int getGreen()
    {
        return (rgb >> 8) & 0xff;
    }

    /**
     * Returns the blue component in the range 0-255
     *
     * @return The blue component
     */
    public int getBlue()
    {
        return rgb & 0xff;
    }

    /**
     * Returns the alpha component in the range 0-255
     *
     * @return The alpha component
     */
    public int getAlpha()
    {
        return (rgb >> 24) & 0xff;
    }

    /**
     * Parses the values from a {@link JsonElement} and updates all
     * {@link Config} values in the objects
     *
     * @param e The Json element
     */
    @Override
    public void fromJson(JsonElement e)
    {
        // value of json element
        String sval = e.getAsString();

        // update rgb value
        int val = Integer.parseInt(sval.substring(1));
        setValue(val);
    }

    /**
     * Returns configs as a string which will be passed to the
     * {@link ConfigFile} writer and written to a <tt>.json</tt> file
     *
     * @return The configs as a parsable Json string
     */
    @Override
    public JsonElement toJson()
    {
        // hex value of color
        return new JsonPrimitive("#" + Integer.toHexString(rgb));
    }
}
