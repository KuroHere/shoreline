package com.caspian.client.api.config.setting;

import com.caspian.client.api.config.Config;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.*;

/**
 *
 * @author linus
 * @since 1.0
 */
public class ColorConfig extends Config<Color>
{
    // RGB value of the current Color value
    private int rgb;

    public ColorConfig(String name, String desc, Color value)
    {
        super(name, desc, value);
        rgb = value.getRGB();
    }

    public ColorConfig(String name, String desc, Integer rgb)
    {
        this(name, desc, new Color(rgb, (rgb & 0xff000000) != 0xff000000));
    }

    public int getRgb()
    {
        return rgb;
    }

    public int getRed()
    {
        return (rgb >> 16) & 0xff;
    }

    public int getGreen()
    {
        return (rgb >> 8) & 0xff;
    }

    public int getBlue()
    {
        return rgb & 0xff;
    }

    public int getAlpha()
    {
        return (rgb >> 24) & 0xff;
    }

    @Override
    public void setValue(Color val)
    {
        super.setValue(val);
        rgb = val.getRGB();
    }

    /**
     *
     *
     * @param val
     */
    public void setValue(int val)
    {
        Color color = new Color(val, (val & 0xff000000) != 0xff000000);
        setValue(color);
        rgb = val;
    }

    /**
     *
     *
     * @return
     */
    @Override
    public JsonObject toJson()
    {
        JsonObject configObj = super.toJson();
        // hex value for readability
        configObj.addProperty("value", "0x" + Integer.toHexString(rgb));
        return configObj;
    }

    /**
     *
     *
     * @param jsonObj The data as a json object
     */
    @Override
    public void fromJson(JsonObject jsonObj)
    {
        if (jsonObj.has("value"))
        {
            JsonElement element = jsonObj.get("value");
            String hex = element.getAsString();
            setValue(Integer.valueOf(hex.substring(2), 16));
        }
    }
}
