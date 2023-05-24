package com.caspian.client.api.config;

import com.google.gson.JsonObject;

/**
 * Property for configurable data that specifies how it will read/write a
 * <tt>.json</tt> file. Data will be converted to and from {@link JsonObject}.
 *
 * @author linus
 * @since 1.0
 *
 * @see Config
 * @see JsonObject
 */
public interface Configurable
{
    /**
     * Converts all data in the object to a {@link JsonObject}.
     *
     * @return The data as a json object
     */
    JsonObject toJson();

    /**
     * Reads all data from a {@link JsonObject} and updates the values of the
     * data in the object.
     *
     * @param jsonObj The data as a json object
     *
     * @see #toJson()
     */
    void fromJson(JsonObject jsonObj);
}
