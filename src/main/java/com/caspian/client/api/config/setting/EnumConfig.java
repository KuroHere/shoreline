package com.caspian.client.api.config.setting;

import com.caspian.client.api.config.Config;
import com.google.gson.JsonObject;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @param <T>
 */
public class EnumConfig<T extends Enum<?>> extends Config<T>
{
    // Array containing all values of the Enum type.
    private final T[] values;

    // Current Enum value index in "values" array. Used to keep track of
    // current "Mode" in dropdown menu.
    private int index;

    public EnumConfig(String name, String desc, T val, T[] values)
    {
        super(name, desc, val);
        this.values = values;
    }

    public T[] getValues()
    {
        return values;
    }

    /**
     * Returns the next value in the {@link #values} array. If the current
     * {@link #index} is greater than the <tt>values.length</tt>, the current
     * index is wrapped to 0.
     *
     * @return The next Enum value
     */
    public T getNextValue()
    {
        index = index + 1 > values.length - 1 ? 0 : index + 1;
        return values[index];
    }

    /**
     * Returns the next value in the {@link #values} array. If the current
     * {@link #index} is greater than 0, the current index is wrapped to
     * <tt>values.length - 1</tt>.
     *
     * @return The next Enum value
     */
    public T getPreviousValue()
    {
        index = index - 1 < 0 ? values.length - 1 : index - 1;
        return values[index];
    }

    @Override
    public JsonObject toJson()
    {
        return null;
    }

    @Override
    public void fromJson(JsonObject jsonObj)
    {

    }
}
