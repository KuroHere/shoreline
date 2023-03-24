package com.momentum.api.config.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.momentum.api.config.Config;
import com.momentum.api.config.file.ConfigFile;

/**
 * Number configuration associated with {@link } in
 * {@link com.momentum.impl.ui.ClickGuiScreen}. Calling
 * {@link Config#setValue(Object)} will check the min and max bounds before
 * updating the value.
 *
 * @author linus
 * @since 03/20/2023
 */
public class NumberConfig extends Config<Number> {

    // min and max bounds
    private final Number min, max;

    /**
     * Initializes the configuration
     *
     * @param name The config name
     * @param desc The config description
     * @param min The minimum config value
     * @param value The config value
     * @param max The maximum config value
     */
    public NumberConfig(String name, String desc, Number min,
                        Number value, Number max)
    {
        super(name, desc, value);
        this.min = min;
        this.max = max;
    }

    /**
     * Sets the configuration value
     *
     * @param val The new configuration value
     * @throws NullPointerException if val is null
     */
    @Override
    public void setValue(Number val) {

        // null check
        if (val == null)
        {
            throw new NullPointerException("Null values not supported");
        }

        // check inbounds min
        if (val.doubleValue() < min.doubleValue())
        {
            value = min;
        }

        // check inbounds max
        else if (val.doubleValue() > max.doubleValue())
        {
            value = max;
        }

        // inbounds
        else
        {
            value = val;
        }
    }

    /**
     * Gets the rounding scale for the value
     *
     * @return The value rounding scale
     */
    public int getRoundingScale() {

        // value as a string
        String val = String.valueOf(value);

        // rounding scale
        return val.substring(val.indexOf(".") + 1).length();
    }

    /**
     * Gets the minimum bound
     *
     * @return The minimum bound
     */
    public Number getMin() {
        return min;
    }

    /**
     * Gets the maximum bound
     *
     * @return The maximum bound
     */
    public Number getMax() {
        return max;
    }

    /**
     * Parses the values from a {@link JsonElement} and updates all
     * {@link Config} values in the objects
     *
     * @param e The Json element
     */
    @Override
    public void fromJson(JsonElement e) {

        // int config
        if (value instanceof Integer)
        {

            // set value to the json element value
            int val = e.getAsInt();
            setValue(val);
        }

        // float config
        else if (value instanceof Float)
        {

            // set value to the json element value
            float val = e.getAsFloat();
            setValue(val);
        }

        // double config
        else if (value instanceof Double)
        {

            // set value to the json element value
            double val = e.getAsDouble();
            setValue(val);
        }
    }

    /**
     * Returns configs as a string which will be passed to the
     * {@link ConfigFile} writer and written to a <tt>.json</tt> file
     *
     * @return The configs as a parsable Json string
     */
    @Override
    public JsonElement toJson() {

        // number to string
        return new JsonPrimitive(value.toString());
    }
}

