package com.momentum.api.config.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.momentum.api.config.Config;
import com.momentum.api.config.Macro;
import com.momentum.api.config.file.ConfigFile;
import com.momentum.impl.ui.click.ClickGuiScreen;
import org.lwjgl.input.Keyboard;

/**
 * Macro configuration associated with {@link } in
 * {@link ClickGuiScreen}.
 *
 * @author linus
 * @since 03/20/2023
 */
public class MacroConfig extends Config<Macro>
{
    /**
     * Config default constructor
     *
     * @param name  The config name
     * @param desc  The config description
     * @param value The config value
     */
    public MacroConfig(String name, String desc, Macro value)
    {
        super(name, desc, value);
    }

    /**
     * Macro Config alternate constructor using LWJGl keycode
     *
     * @param name  The config name
     * @param desc  The config description
     * @param keycode The config value keycode
     * @param action The config value action
     * @throws IllegalArgumentException if keycode is not a valid LWJGL keycode
     */
    public MacroConfig(String name, String desc, Integer keycode,
                       Runnable action)
    {
        super(name, desc, new Macro(keycode, action));
    }

    /**
     * Sets the macro value using LWJGl keycode
     *
     * @param keycode The config value keycode
     * @throws IllegalArgumentException if keycode is not a valid LWJGL keycode
     */
    public void setValue(Integer keycode)
    {
        // create new macro
        value = new Macro(keycode, value.getAction());
    }

    /**
     * Sets the macro value using LWJGl keycode
     *
     * @param keycode The config value keycode
     * @param action The config value action
     * @throws IllegalArgumentException if keycode is not a valid LWJGL keycode
     */
    public void setValue(Integer keycode, Runnable action)
    {
        // create new macro
        value = new Macro(keycode, action);
    }

    /**
     * Parses the values from a {@link JsonElement} and updates all
     * {@link Config} values in the objects
     *
     * @param e The Json element
     */
    @Override
    public void fromJson(JsonElement e)
    {
        // value of json element
        String sval = e.getAsString();

        // update key index based on LWJGL mappings
        int val = Keyboard.getKeyIndex(sval);
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
        // keybinding key name
        return new JsonPrimitive(value.getKeyName());
    }
}
