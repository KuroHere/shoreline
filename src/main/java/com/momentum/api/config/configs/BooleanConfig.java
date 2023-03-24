package com.momentum.api.config.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.momentum.api.config.Config;
import com.momentum.api.config.file.ConfigFile;

/**
 * Boolean configuration associated with {@link } in
 * {@link com.momentum.impl.ui.ClickGuiScreen}.
 *
 * @author linus
 * @since 03/20/2023
 */
public class BooleanConfig extends Config<Boolean> {

    /**
     * Config default constructor
     *
     * @param name  The config name
     * @param desc  The config description
     * @param value The config value
     */
    public BooleanConfig(String name, String desc, Boolean value)
    {
        super(name, desc, value);
    }

    /**
     * Parses the values from a {@link JsonElement} and updates all
     * {@link Config} values in the objects
     *
     * @param e The Json element
     */
    @Override
    public void fromJson(JsonElement e) {

        // set value to the json element value
        boolean val = e.getAsBoolean();
        setValue(val);
    }

    /**
     * Returns configs as a string which will be passed to the
     * {@link ConfigFile} writer and written to a <tt>.json</tt> file
     *
     * @return The configs as a parsable Json string
     */
    @Override
    public JsonElement toJson() {

        // boolean value to string
        return new JsonPrimitive(value.toString());
    }
}
