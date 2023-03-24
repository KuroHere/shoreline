package com.momentum.api.config.factory;

import com.momentum.api.config.Config;
import com.momentum.api.config.Configuration;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 *
 * @author linus
 * @since 03/22/2023
 */
public class ReflectionConfigFactory {

    /**
     * @param clazz
     * @return
     */
    public Map<String, Config<?>> build(Class<?> clazz) {

        // configs in class
        Map<String, Config<?>> configs = new LinkedHashMap<>();

        // all declared fields in the class
        for (Field f : clazz.getDeclaredFields())
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

                    // put in configs list
                    configs.put(id, config);
                }

                // field getter error
                catch (IllegalArgumentException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }

        // configs in class
        return configs;
    }
}
