package com.caspian.api.config;

import com.caspian.Caspian;

import java.lang.reflect.Field;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see ConfigContainer
 */
public class ConfigFactory
{
    /**
     * Creates and returns a new {@link Config} instance from a {@link Field}
     * using Java reflection lib.
     *
     * @param f The config field
     * @return The created config
     * @throws RuntimeException if the field is not a Config type or reflect
     * could not access the field
     */
    public Config<?> build(Field f)
    {
        // attempt to extract object from field
        if (f.trySetAccessible())
        {
            try
            {
                return (Config<?>) f.get(this);
            }

            // field getter error
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                Caspian.error("Failed to build config from field " + f.getName());
                e.printStackTrace();
            }
        }

        // failed config creation
        throw new RuntimeException("Invalid field!");
    }
}