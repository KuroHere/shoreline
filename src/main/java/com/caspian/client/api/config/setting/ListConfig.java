package com.caspian.client.api.config.setting;

import com.caspian.client.api.config.Config;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @param <T>
 */
public class ListConfig<T> extends Config<List<T>>
{
    /**
     * Initializes the config with a default value. This constructor should
     * not be used to initialize a configuration, instead use the explicit
     * definitions of the configs in {@link com.caspian.client.api.config.setting}.
     *
     * @param name  The unique config identifier
     * @param desc  The config description
     * @param value The default config value
     * @throws NullPointerException if value is <tt>null</tt>
     */
    public ListConfig(String name, String desc, List<T> value)
    {
        super(name, desc, value);
    }

    /**
     * Initializes the config with a default value. This constructor should
     * not be used to initialize a configuration, instead use the explicit
     * definitions of the configs in {@link com.caspian.client.api.config.setting}.
     *
     * @param name  The unique config identifier
     * @param desc  The config description
     * @param values The default config values
     * @throws NullPointerException if value is <tt>null</tt>
     */
    @SafeVarargs
    public ListConfig(String name, String desc, T... values)
    {
        super(name, desc, Arrays.stream(values).toList());
    }

    /**
     *
     *
     * @param e
     * @return
     */
    public boolean contains(T e)
    {
        return getValue().contains(e);
    }

    /**
     * Converts all data in the object to a {@link JsonObject}.
     *
     * @return The data as a json object
     */
    @Override
    public JsonObject toJson()
    {
        return null;
    }

    /**
     * Reads all data from a {@link JsonObject} and updates the values of the
     * data in the object.
     *
     * @param jsonObj The data as a json object
     * @see #toJson()
     */
    @Override
    public void fromJson(JsonObject jsonObj)
    {

    }
}
