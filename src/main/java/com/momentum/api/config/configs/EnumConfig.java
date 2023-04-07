package com.momentum.api.config.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.momentum.api.config.Config;
import com.momentum.api.config.file.ConfigFile;
import com.momentum.impl.gui.click.ClickGuiScreen;

/**
 * Enum configuration associated with {@link } in
 * {@link ClickGuiScreen}.
 *
 * @author linus
 * @since 03/20/2023
 */
public class EnumConfig<T extends Enum<T>> extends Config<T>
{
    // mode values
    private final T[] values;

    /**
     * Initializes the configuration
     *
     * @param name  The config name
     * @param desc  The config description
     * @param values The config values
     * @param value The selected config value
     */
    public EnumConfig(String name, String desc, T[] values, T value)
    {
        super(name, desc, value);
        this.values = values;
    }

    /**
     * Gets the configuration mode values
     *
     * @return The configuration mode values
     */
    public T[] getValues()
    {
        return values;
    }

    /**
     * Parses the values from a {@link JsonElement} and updates all
     * {@link Config} values in the objects
     *
     * @param e The Json element
     */
    @SuppressWarnings("unchecked")
    @Override
    public void fromJson(JsonElement e)
    {
        // value of the json element
        String sval = e.getAsString();

        // enum value
        T val = (T) Enum.valueOf(value.getClass(), sval);

        // update option value
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
        // enum value to string
        return new JsonPrimitive(value.toString());
    }
}
