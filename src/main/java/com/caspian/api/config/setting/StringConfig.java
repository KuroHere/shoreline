package com.caspian.api.config.setting;

import com.caspian.api.config.Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 *
 */
public class StringConfig extends Config<String>
{
    /**
     * Initializes the config with a default value. This constructor should
     * not be used to initialize a configuration, instead use the explicit
     * definitions of the configs in {@link com.caspian.api.config.setting}.
     *
     * @param name  The unique config identifier
     * @param desc  The config description
     * @param value The default config value
     * @throws NullPointerException if value is <tt>null</tt>
     */
    public StringConfig(String name, String desc, String value)
    {
        super(name, desc, value);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public JsonObject toJson()
    {
        return new JsonPrimitive(getValue()).getAsJsonObject();
    }

    /**
     * @param jsonObj
     */
    @Override
    public void fromJson(JsonObject jsonObj)
    {
        setValue(jsonObj.getAsString());
    }
}
