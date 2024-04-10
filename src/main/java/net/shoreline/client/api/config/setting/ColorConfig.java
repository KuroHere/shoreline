package net.shoreline.client.api.config.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.init.Modules;

import java.awt.*;
import java.util.function.Supplier;

/**
 * @author linus
 * @since 1.0
 */
public class ColorConfig extends Config<Color> {
    // RGB value of the current Color value
    private float[] hsb;
    private boolean allowAlpha;
    //
    private boolean global;

    public ColorConfig(String name, String desc, Color value, boolean allowAlpha) {
        super(name, desc, value);
        float[] hsbVals = Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), null);
        hsb = new float[] { hsbVals[0], hsbVals[1], hsbVals[2], value.getAlpha() / 255.0f};
        this.allowAlpha = allowAlpha;
    }

    public ColorConfig(String name, String desc, Color value) {
        this(name, desc, value, true);
    }

    public ColorConfig(String name, String desc, Color value, Supplier<Boolean> visible) {
        super(name, desc, value, visible);
        float[] hsbVals = Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), null);
        hsb = new float[] { hsbVals[0], hsbVals[1], hsbVals[2], value.getAlpha() / 255.0f};
    }

    public ColorConfig(String name, String desc, Integer rgb, boolean allowAlpha) {
        this(name, desc, new Color(rgb, (rgb & 0xff000000) != 0xff000000), allowAlpha);
    }

    public ColorConfig(String name, String desc, Integer value) {
        this(name, desc, value, false);
    }

    @Override
    public void setValue(Color val) {
        super.setValue(val);
        float[] hsbVals = Color.RGBtoHSB(val.getRed(), val.getGreen(), val.getBlue(), null);
        hsb = new float[] { hsbVals[0], hsbVals[1], hsbVals[2], val.getAlpha() / 255.0f};
    }

    /**
     * @param val
     */
    public void setValue(int val) {
        Color color = new Color(val, (val & 0xff000000) != 0xff000000);
        setValue(color);
    }

    public int getRgb() {
        return value.getRGB();
    }

    public int getRed() {
        return value.getRed();
    }

    public int getGreen() {
        return value.getGreen();
    }

    public int getBlue() {
        return value.getBlue();
    }

    public boolean allowAlpha() {
        return allowAlpha;
    }

    public int getAlpha() {
        return value.getAlpha();
    }

    public float[] getHsb() {
        return hsb;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal() {
        setGlobal(true);
    }

    public void setGlobal(boolean global) {
        this.global = global;
        configAnimation.setState(global);
        if (global && Modules.CLICK_GUI != null) {
            setValue(Modules.CLICK_GUI.getColor(220));
        }
    }

    /**
     * @return
     */
    @Override
    public JsonObject toJson() {
        JsonObject configObj = super.toJson();
        // hex value for readability
        configObj.addProperty("value", "0x" + Integer.toHexString(getRgb()));
        configObj.addProperty("global", global);
        return configObj;
    }

    /**
     * @param jsonObj The data as a json object
     * @return
     */
    @Override
    public Color fromJson(JsonObject jsonObj) {
        if (jsonObj.has("value")) {
            JsonElement element = jsonObj.get("value");
            String hex = element.getAsString();
            if (jsonObj.has("global"))
            {
                JsonElement element1 = jsonObj.get("global");
                setGlobal(element1.getAsBoolean());
            }
            return parseColor(hex);
        }
        return null;
    }

    /**
     * @param colorString
     * @return
     * @throws IllegalArgumentException
     */
    private Color parseColor(String colorString) {
        if (colorString.startsWith("0x")) {
            colorString = colorString.substring(2);
            return new Color((int) Long.parseLong(colorString, 16));
        }
        throw new IllegalArgumentException("Unknown color: " + colorString);
    }
}
