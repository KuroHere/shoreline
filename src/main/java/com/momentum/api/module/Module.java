package com.momentum.api.module;

<<<<<<< Updated upstream
import com.momentum.api.event.Listener;
import com.momentum.api.feature.Option;
import com.momentum.api.feature.Feature;
import com.momentum.api.feature.IToggleable;
import com.momentum.api.registry.ILabel;
import com.momentum.Momentum;
import org.lwjgl.input.Keyboard;
=======
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.momentum.api.config.Config;
import com.momentum.api.config.Configuration;
import com.momentum.api.config.Macro;
import com.momentum.api.config.configs.BooleanConfig;
import com.momentum.api.config.factory.ConfigContainer;
import com.momentum.api.config.file.ConfigFile;
import com.momentum.api.config.file.IConfigurable;
import com.momentum.api.module.exceptions.IncompatibleInterfaceException;
import com.momentum.api.module.property.IConcurrent;
import com.momentum.api.module.property.IHideable;
import com.momentum.api.module.property.IToggleable;
import com.momentum.api.registry.ILabeled;
import com.momentum.api.util.Globals;
>>>>>>> Stashed changes

import java.util.Map.Entry;

/**
 * Configurable client feature that is displayed in the
 * {@link com.momentum.impl.ui.ClickGuiScreen} and the Hud. Modules can be
 * toggled using a {@link Macro}. Modules can be hidden from Hud.
 *
 * @author linus
 * @since 03/20/2023
 *
 * @see com.momentum.api.module.modules.SubscriberModule
 * @see com.momentum.api.module.modules.ConcurrentModule
 * @see com.momentum.api.module.modules.ToggleModule
 */
public class Module extends ConfigContainer
        implements Globals, IConfigurable<JsonObject>, IHideable, ILabeled {

    // module identifier
    // must be unique for each module
    private final String name;

    // description of module functionality
    private final String desc;

    // module category
    private final ModuleCategory category;

    // hidden state
    // default set to false
    @Configuration("module_hidden")
    final BooleanConfig hidden = new BooleanConfig("Hidden",
            "Hidden state. Global in all modules. This config determines " +
                    "whether the module will appear in the Hud", false);

    /**
     * Default module constructor. Name must be unique for all modules.
     *
     * @param name The module name
     * @param desc The module description
     * @param category The module category
     * @throws IncompatibleInterfaceException if module implements incompatible
     * interfaces
     */
    public Module(String name, String desc, ModuleCategory category)
    {
        // {@see ReflectionConfigFactory#build(Class)}
        super();

        // incompatible module interfaces
        if (this instanceof IToggleable
                && this instanceof IConcurrent)
        {
            throw new IncompatibleInterfaceException(
                    "Module cannot implement IToggleable and IConcurrent at " +
                            "the same time");
        }

        // init
        this.name = name;
        this.desc = desc;
        this.category = category;
    }

    /**
     * Gets the module name
     *
     * @return The module name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the module description
     *
     * @return The module description
     */
    public String getDescription() {
        return desc;
    }

    /**
     * Gets the {@link ModuleCategory} module category
     *
<<<<<<< Updated upstream
     * @param in The new drawn state
     */
    public void draw(boolean in) {

        // set the drawn config
        drawn.setVal(in);
    }

    /**
     * Called when the feature is bound to a key
     */
    @Override
    public void onBind() {

    }

    /**
     * Toggles the enabled state
     */
    @Override
    public void toggle() {

        // check enabled state
        if (enabled.getVal()) {
            enabled.setVal(false);
            onDisable();
        }

        // not enabled
        else {
            enabled.setVal(true);
            onEnable();
        }

        // event
        onToggle();
    }

    @Override
    public void onToggle() {

    }

    /**
     * Enables the module
     */
    @Override
    public void enable() {

        // enabled state = true
        enabled.setVal(true);
        onEnable();
    }

    /**
     * Called when feature is enabled
     */
    @Override
    public void onEnable() {

        // subscribe all listeners
        for (Listener d : listeners) {

            // subscribe
            Momentum.EVENT_BUS.subscribe(d);
        }
    }

    /**
     * Disables the module
     */
    @Override
    public void disable() {

        // enabled state = false
        enabled.setVal(false);
        onDisable();
    }

    /**
     * Called when feature is disabled
     */
    @Override
    public void onDisable() {

        // subscribe all listeners
        for (Listener d : listeners) {

            // subscribe
            Momentum.EVENT_BUS.unsubscribe(d);
        }
    }

    /**
     * Gets the registry label
     *
     * @return The registry label (Must be unique!)
     */
    @Override
    public String getLabel() {

        // create label
        return name.toLowerCase() + "_module";
    }

    /**
     * Associates options with this module
     *
     * @param config The options
     */
    public void associate(Option... config) {

        // associate all
        for (Option option : config) {
            
            // create association
            // add to list of associated options
            option.associate(this);
            options.add(option);
        }
    }

    /**
     * Associates listeners with this module
     *
     * @param config The listeners
     */
    public void associate(Listener... config) {

        // associate all
        // add to list of associated listeners
        listeners.addAll(Arrays.asList(config));
    }

    /**
     * Gets the module's category
     *
     * @return The module's category
=======
     * @return The module category
>>>>>>> Stashed changes
     */
    public ModuleCategory getCategory() {
        return category;
    }

    /**
     * Returns whether the object is hidden
     *
     * @return The hidden state
     */
    @Override
    public boolean isHidden() {

        // hidden state val
        return hidden.getValue();
    }

    /**
     * Sets the object's hidden state
     *
     * @param hide The new hide state
     */
    @Override
    public void setHidden(boolean hide) {

        // update hidden state val
        hidden.setValue(hide);
    }

    /**
     * Gets the module label
     *
     * @return The module label
     */
    @Override
    public String getLabel() {

        // module label
        return name.toLowerCase() + "_module";
    }

    /**
     * Parses the values from a {@link JsonObject} and updates all
     * {@link Config} values in the objects
     *
     * @param o The Json object
     */
    @Override
    public void fromJson(JsonObject o) {

        // JsonElement set
        for (Entry<String, JsonElement> entry : o.entrySet())
        {
            // cfg from key
            Config<?> cfg = retrieve(entry.getKey());

            // check retrieved
            if (cfg != null)
            {
                // catches read exceptions
                try
                {
                    // parse Json value
                    cfg.fromJson(entry.getValue());
                }

                // couldn't parse Json value
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns configs as a string which will be passed to the
     * {@link ConfigFile} writer and written to a <tt>.json</tt> file
     *
     * @return The configs as a parsable Json string
     */
    @Override
    public JsonObject toJson() {

        // json object
        JsonObject out = new JsonObject();

        // write all configurations
        for (Config<?> cfg : getConfigs())
        {

            // toggleable configs
            if (cfg.getName().equalsIgnoreCase("Enabled")
                    || cfg.getName().equalsIgnoreCase("Bind"))
            {

                // concurrent module
                if (this instanceof IConcurrent) {

                    // skip config
                    continue;
                }
            }

            // JsonElement from config value
            JsonElement e = cfg.toJson();

            // add to output
            out.add(cfg.getLabel(), e);
        }

        // output JsonObject
        return out;
    }
}
