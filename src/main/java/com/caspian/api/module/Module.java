package com.caspian.api.module;

import com.caspian.api.config.Config;
import com.caspian.api.config.ConfigContainer;
import com.caspian.api.config.Configurable;
import com.caspian.util.Globals;

/**
 * General client feature that will appear in the ClickGui. Module have a
 * unique name which is also used as the module identifier. Modules are
 * grouped by {@link ModuleCategory}.
 *
 * <p>Modules are {@link Configurable} and hold {@link Config}
 * in a {@link ConfigContainer}. Modules can be configured in the ClickGui
 * or through the use of Commands in the chat.</p>
 *
 * @author linus
 * @since 1.0
 *
 * @see ToggleModule
 * @see ConcurrentModule
 */
public class Module extends ConfigContainer implements Globals
{
    // Concise module description, displayed in the ClickGui to help users
    // understand the functionality of the module.
    private final String desc;

    // Modules are grouped into categories for easy navigation in the
    // ClickGui. Modules with ModuleCategory.TEST category are not available
    // to the user.
    private final ModuleCategory category;

    /**
     *
     *
     * @param name     The unique module identifier
     * @param desc     The module description
     * @param category The module category
     */
    public Module(String name, String desc, ModuleCategory category)
    {
        super(name);
        this.desc = desc;
        this.category = category;
    }

    /**
     *
     *
     * @return
     *
     * @see ModuleManager#getModule(String)
     * @see ModulePreset#save()
     */
    public String getRef()
    {
        return getName().toLowerCase() + "_module";
    }

    /**
     *
     *
     * @return
     */
    public String getDescription()
    {
        return desc;
    }

    /**
     * Returns the {@link ModuleCategory} of the module.
     *
     * @return The category of the module
     *
     * @see ModuleCategory
     */
    public ModuleCategory getCategory()
    {
        return category;
    }

    /**
     * Returns the value of the {@link Config} associated with the param
     * reference.
     *
     * @param ref The configuration reference
     * @return The config value of the configuration associated with the
     * param reference
     * @param <T> The config value type
     *
     * @see #getConfig(String)
     * @see Config#getValue()
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String ref)
    {
        return ((Config<T>) getConfig(ref)).getValue();
    }
}
