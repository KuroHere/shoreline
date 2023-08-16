package com.caspian.client.api.config;

import com.caspian.client.Caspian;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Container for {@link Config} backed by a {@link ConcurrentMap}. Manages
 * all declared configurations for the container class.
 *
 * @author linus
 * @since 1.0
 *
 * @see Config
 * @see ConfigFactory
 */
public class ConfigContainer implements Configurable
{
    // Container name is its UNIQUE identifier.
    private final String name;
    // List of all configurations in the container. The configs are managed
    // by a Map with references to their data tags.
    private final Map<String, Config<?>> configurations =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Uses the reflection {@link ConfigFactory} to add all declared configurations
     * to the config {@link ConcurrentMap}. Declared {@link Config}s will not
     * be registered if this process does not complete.
     *
     * @param name The container name
     *
     * @see ConfigFactory
     */
    public ConfigContainer(String name)
    {
        // set name of this container early
        // DO NOT MOVE THIS BACK TO THE BOTTOM - aesthetical
        this.name = name;
    }

    /**
     *
     * @param configs
     */
    protected void register(Config<?>... configs)
    {
        for (Config<?> config : configs)
        {
            configurations.put(config.getId(), config);
        }
    }

    /**
     * Reflect configuration fields
     */
    public void reflectConfigs()
    {
        final ConfigFactory factory = new ConfigFactory(this);
        // populate container using reflection
        for (Field field : getClass().getDeclaredFields())
        {
            if (Config.class.isAssignableFrom(field.getType()))
            {
                Config<?> product = factory.build(field);
                if (product == null)
                {
                    // failsafe for debugging purposes
                    Caspian.error("Value for field {} is null", field);
                    continue;
                }
                product.setContainer(this);
                configurations.put(product.getId(), product);
            }
        }
    }

    /**
     * Returns the container as a {@link JsonObject} containing a list of the
     * registered {@link Config}
     *
     * @return The container as a json object
     *
     * @see Config#toJson()
     */
    @Override
    public JsonObject toJson()
    {
        final JsonObject out = new JsonObject();
        final JsonArray array = new JsonArray();
        for (Config<?> config : getConfigs())
        {
            array.add(config.toJson());
        }
        out.add("configs", array);
        return out;
    }

    /**
     * Reads the configuration values from a {@link JsonObject} and updates
     * the {@link Config} values.
     *
     * @param jsonObj The container as a json object
     *
     * @see Config#fromJson(JsonObject)
     */
    @Override
    public void fromJson(JsonObject jsonObj)
    {
        if (jsonObj.has("configs"))
        {
            JsonElement element = jsonObj.get("configs");
            if (!element.isJsonArray())
            {
                return;
            }
            for (JsonElement je : element.getAsJsonArray())
            {
                if (je.isJsonObject())
                {
                    final JsonObject configObj = je.getAsJsonObject();
                    final JsonElement id = configObj.get("id");
                    Config<?> config = getConfig(id.getAsString());
                    if (config != null)
                    {
                        try
                        {
                            config.fromJson(configObj);
                        }
                        // couldn't parse Json value
                        catch (Exception e)
                        {
                            Caspian.error("Couldn't parse Json for {}",
                                    config.getName());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the container unique name identifier. This name will be
     * displayed to the users when representing this container.
     *
     * @return The unique name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the {@link Config} from the reference {@link Config#getId()}
     * data tag in the registry.
     *
     * @param id The config data tag
     * @return The config from the id
     *
     * @see Config#getId()
     */
    public Config<?> getConfig(String id)
    {
        return configurations.get(id);
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
