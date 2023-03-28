package com.momentum.api.config.factory;

import com.momentum.api.config.Config;
import com.momentum.api.config.Configuration;

import java.lang.reflect.Field;

/**
 * Reflection based {@link Config} factory
 *
 * @author linus
 * @since 03/22/2023
 */
public class ConfigFactory
{
    /**
     * Builds a {@link Config} from a jvm {@link Field}
     *
     * @param f The config field
     * @return The built config
     */
    public Config<?> build(Field f)
    {
        // check field type
        if (f.isAnnotationPresent(Configuration.class))
        {
            // set field accessible
            f.setAccessible(true);

            // config id from annotation
            String id = f.getAnnotation(Configuration.class).value();

            // catches IllegalArgumentException and IllegalAccessException
            try
            {
                // config field
                final Config<?> config = (Config<?>) f.get(this);

                // set config label to annotation id
                config.setLabel(id);
                return config;
            }

            // field getter error
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        // failed build
        return null;
    }
}
