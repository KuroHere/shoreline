package com.caspian.client.api.config.setting;

import com.caspian.client.api.config.Config;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class BooleanConfig extends Config<Boolean>
{
    public BooleanConfig(String name, String desc, Boolean val)
    {
        super(name, desc, val);
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
