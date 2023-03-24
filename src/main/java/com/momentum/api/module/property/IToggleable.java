package com.momentum.api.module.property;

import com.momentum.api.config.Macro;

/**
 * Toggleable object that allows modification of an enabled state. Supports
 * keybindings through the use of {@link Macro}.
 *
 * @author linus
 * @since 03/20/2023
 */
public interface IToggleable {

    /**
     * Binds the object to a keybinding
     *
     * @param key The bind keycode
     * @throws IllegalArgumentException if key is not a valid keycode on the
     * LWJGL keyboard
     */
    void bind(int key);

    /**
     * Toggles the enable state
     */
    void toggle();

    /**
     * Enables the feature
     */
    void enable();

    /**
     * Disables the object
     */
    void disable();

    /**
     * Called when the object is bound to a key
     */
    void onBind();

    /**
     * Called when the object is toggled
     */
    void onToggle();

    /**
     * Called when the object is enabled
     */
    void onEnable();

    /**
     * Called when the object is disabled
     */
    void onDisable();

    /**
     * Returns whether the enabled state is true
     *
     * @return The enabled state = true
     */
    boolean isEnabled();
}
