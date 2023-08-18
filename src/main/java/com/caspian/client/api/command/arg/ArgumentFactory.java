package com.caspian.client.api.command.arg;

import com.caspian.client.Caspian;
import java.lang.reflect.Field;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ArgumentFactory
{
    //
    private final Object argObj;

    /**
     *
     * @param argObj
     */
    public ArgumentFactory(Object argObj)
    {
        this.argObj = argObj;
    }

    /**
     * Creates and returns a new {@link Argument} instance from a {@link Field}
     * using Java reflection lib.
     *
     * @param f The arg field
     * @return The created config
     * @throws RuntimeException if the field is not a Config type or reflect
     * could not access the field
     */
    public Argument<?> build(Field f)
    {
        f.setAccessible(true);
        // attempt to extract object from field
        try
        {
            return (Argument<?>) f.get(argObj);
        }
        // field getter error
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            Caspian.error("Failed to build argument from field {}!",
                    f.getName());
            e.printStackTrace();
        }
        // failed arg creation
        throw new RuntimeException("Invalid field!");
    }
}
