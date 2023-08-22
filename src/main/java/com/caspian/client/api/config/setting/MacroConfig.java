package com.caspian.client.api.config.setting;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.macro.Macro;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MacroConfig extends Config<Macro>
{
    public MacroConfig(String name, String desc, Macro val)
    {
        super(name, desc, val);
    }

    /**
     * Overloaded method {@link Config#setValue(Object)}. Sets value by
     * instantiating new {@link Macro} based on method parameters.
     *
     * @param keycode The macro keycode
     */
    public void setValue(int keycode)
    {
        getValue().setKeycode(keycode);
    }

    public Runnable getRunnable()
    {
        return getValue().getRunnable();
    }

    public int getKeycode()
    {
        return getValue().getKeycode();
    }

    public String getKeyName()
    {
        return getValue().getKeyName();
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject configObj = super.toJson();
        configObj.addProperty("value", getKeycode());
        return configObj;
    }

    @Override
    public void fromJson(JsonObject jsonObj)
    {
        if (jsonObj.has("value"))
        {
            JsonElement element = jsonObj.get("value");
            setValue(element.getAsInt());
        }
    }
}
