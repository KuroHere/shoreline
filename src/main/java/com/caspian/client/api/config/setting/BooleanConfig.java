package com.caspian.client.api.config.setting;

import com.caspian.client.api.config.Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.function.Supplier;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BooleanConfig extends Config<Boolean>
{
    public BooleanConfig(String name, String desc, Boolean val)
    {
        super(name, desc, val);
    }

    public BooleanConfig(String name, String desc, Boolean val,
                         Supplier<Boolean> visible)
    {
        super(name, desc, val, visible);
    }

    @Override
    public JsonObject toJson()
    {
        return new JsonPrimitive(getValue()).getAsJsonObject();
    }

    @Override
    public void fromJson(JsonObject jsonObj)
    {

    }
}
