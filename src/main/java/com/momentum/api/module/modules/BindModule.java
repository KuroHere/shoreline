package com.momentum.api.module.modules;

import com.momentum.api.config.Configuration;
import com.momentum.api.config.Macro;
import com.momentum.api.config.configs.MacroConfig;
import com.momentum.api.module.ModuleCategory;
import com.momentum.api.module.exceptions.IncompatibleInterfaceException;
import com.momentum.api.module.property.IBindable;
import org.lwjgl.glfw.GLFW;

/**
 *
 *
 * @author linus
 * @since 03/26/2023
 */
public class BindModule extends ToggleModule implements IBindable
{
    // global module keybind
    // default set to Keyboard.KEY_NONE
    @Configuration("module_keybind")
    final MacroConfig keybind = new MacroConfig("Bind",
            "Bind state. Global config in toggleable modules. Currently " +
                    "supports LWJGL keyboard and mouse keybindings",
            GLFW.GLFW_KEY_UNKNOWN, this::toggle);

    /**
     * Default module constructor. Name must be unique for all modules.
     *
     * @param name     The module name
     * @param desc     The module description
     * @param category The module category
     * @throws IncompatibleInterfaceException if module implements incompatible
     *                                        interfaces
     */
    public BindModule(String name, String desc, ModuleCategory category)
    {
        super(name, desc, category);
    }

    /**
     * Initializes the module with a keybind
     *
     * @param name The module name
     * @param desc     The module description
     * @param category The module category
     * @throws IncompatibleInterfaceException if module implements incompatible
     *                                        interfaces
     */
    public BindModule(String name, String desc, ModuleCategory category,
                      Integer keybind)
    {
        super(name, desc, category);
        bind(keybind);
    }

    /**
     * Binds the module to a keycode
     *
     * @param key The bind keycode
     * @throws IllegalArgumentException if key is not a valid keycode
     *                                  on the LWJGL keyboard
     */
    @Override
    public void bind(int key)
    {
        // bind to keycode
        keybind.setValue(key);
        onBind();
    }

    /**
     * Binds the module to a macro
     *
     * @param m The bind macro
     * @throws IllegalArgumentException if key is not a valid keycode
     *                                  on the LWJGL keyboard
     */
    @Override
    public void bind(Macro m)
    {
        // bind to keycode
        keybind.setValue(m);
        onBind();
    }

    /**
     * Called when the object is bound to a key
     */
    @Override
    public void onBind()
    {
        // impl in module
    }

    /**
     * Returns the bind macro
     *
     * @return The bind macro
     */
    @Override
    public Macro getKeybinding()
    {
        return keybind.getValue();
    }

    /**
     * Returns the bind keycode
     *
     * @return The bind keycode
     */
    public int getKeycode()
    {
        return getKeybinding().getKeycode();
    }
}
