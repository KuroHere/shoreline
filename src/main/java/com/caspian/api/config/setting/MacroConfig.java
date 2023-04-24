package com.caspian.api.config.setting;

import com.caspian.api.config.Config;
import com.caspian.api.macro.Macro;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
     * @param macro   The macro runnable
     */
    public void setValue(int keycode, Runnable macro)
    {
        setValue(new Macro(getRef(), keycode, macro));
    }

    @Override
    public JsonObject toJson()
    {
        return new JsonPrimitive(getValue().keycode()).getAsJsonObject();
    }

    @Override
    public void fromJson(JsonObject jsonObj)
    {

    }
}
