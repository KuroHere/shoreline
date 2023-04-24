package com.caspian.api.config;

import com.caspian.Caspian;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Container for {@link Config} backed by a {@link ConcurrentMap}.
 *
 * @author linus
 * @since 1.0
 *
 * @see Config
 */
public class ConfigContainer implements Configurable
{
    // Container name is its UNIQUE identifier
    private final String name;

    //
    private final ConcurrentMap<String, Config<?>> configurations =
            new ConcurrentHashMap<>();

    /**
     *
     *
     * @param name
     */
    public ConfigContainer(String name)
    {
        this.name = name;
        // populate container using reflection
        ConfigFactory factory = new ConfigFactory();
        for (Field field : getClass().getDeclaringClass().getDeclaredFields())
        {
            if (Config.class.isAssignableFrom(field.getType()))
            {
                Config<?> product = factory.build(field);
                product.setContainer(this);
                configurations.put(product.getRef(), product);
            }
        }
    }

    /**
     *
     *
     * @return
     */
    @Override
    public JsonObject toJson()
    {
        // write all configurations
        JsonObject out = new JsonObject();
        for (Config<?> config : getConfigs())
        {
            out.add(config.getRef(), config.toJson());
        }

        return out;
    }

    /**
     *
     *
     * @param jsonObj
     */
    @Override
    public void fromJson(JsonObject jsonObj)
    {
        for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet())
        {
            // config from ref
            Config<?> config = getConfig(entry.getKey());
            if (config != null)
            {
                try
                {
                    config.fromJson(entry.getValue().getAsJsonObject());
                }

                // couldn't parse Json value
                catch (Exception e)
                {
                    Caspian.error("Couldn't parse Json for " + name);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     *
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     *
     *
     * @param ref
     * @return
     */
    public Config<?> getConfig(String ref)
    {
        return configurations.get(ref);
    }

    /**
     *
     *
     * @return
     */
    public Collection<Config<?>> getConfigs()
    {
        return configurations.values();
    }
}
