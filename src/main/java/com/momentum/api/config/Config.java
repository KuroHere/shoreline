package com.momentum.api.config;

import com.google.gson.JsonElement;
import com.momentum.api.config.factory.ConfigContainer;
import com.momentum.api.config.file.ConfigFile;
import com.momentum.api.config.file.IConfigurable;
import com.momentum.api.registry.ILabeled;

/**
 * Config implementation which will be saved locally in a <tt>.json</tt> file
 * and updated based on user input. The value of a config can be updated
 * through commands or the {@link com.momentum.impl.ui.ClickGuiScreen}.
 *
 * @author linus
 * @since 03/20/2023
 * @param <T> The value type
 *
 * @see com.momentum.api.config.configs.BooleanConfig
 * @see com.momentum.api.config.configs.EnumConfig
 * @see com.momentum.api.config.configs.MacroConfig
 * @see com.momentum.api.config.configs.NumberConfig
 */
public abstract class Config<T>
        implements IConfigurable<JsonElement>, ILabeled {

    // config identifier
    // must be unique
    protected String name;
    private String label;

    // description of the functionality or property that
    // the configuration modifies
    protected String desc;

    // value associated with config
    // mutable
    protected T value;

    // configuration container pointer
    protected ConfigContainer container;

    /**
     * Config default constructor
     *
     * @param name The config name
     * @param desc The config description
     * @param value The config value
     */
    public Config(String name, String desc, T value)
    {
        this.name = name;
        this.desc = desc;
        this.value = value;
    }

    /**
     * Returns the name that is most commonly associated with the configuration
     *
     * @return The primary name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the config description
     *
     * @return The config description
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Returns the configuration value
     *
     * @param val The new configuration value
     */
    public void setValue(T val) {
        value = val;
    }

    /**
     * Returns the configuration value
     *
     * @return The configuration value
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the configuration parent {@link ConfigContainer}
     *
     * @param cont The new configuration parent container
     */
    public void setContainer(ConfigContainer cont) {
        container = cont;
    }

    /**
     * Returns the configuration parent {@link ConfigContainer}
     *
     * @return The configuration parent container
     */
    public ConfigContainer getContainer() {
        return container;
    }

    /**
     * Sets the configuration label
     *
     * @param l The new configuration label
     */
    public void setLabel(String l) {
        label = l;
    }

    /**
     * Overwritten by {@link Configuration#value()}. Returns the
     * configuration label.
     *
     * @return The configuration label
     */
    @Override
    public String getLabel() {

        // no impl
        return label;
    }

    /**
     * Parses the values from a {@link JsonElement} and updates all
     * {@link com.momentum.api.config.Config} values in the objects
     *
     * @param e The Json element
     */
    @Override
    public abstract void fromJson(JsonElement e);

    /**
     * Returns configs as a string which will be passed to the
     * {@link ConfigFile} writer and written to a <tt>.json</tt> file
     *
     * @return The configs as a parsable Json string
     */
    @Override
    public abstract JsonElement toJson();
}
