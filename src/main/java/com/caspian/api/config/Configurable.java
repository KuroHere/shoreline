package com.caspian.api.config;

import com.google.gson.JsonObject;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Config
 */
public interface Configurable
{
    /**
     *
     *
     * @return
     */
    JsonObject toJson();

    /**
     *
     *
     * @param jsonObj
     */
    void fromJson(JsonObject jsonObj);
}
