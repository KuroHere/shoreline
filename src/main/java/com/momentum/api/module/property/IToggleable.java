package com.momentum.api.module.property;


/**
 * Toggleable object that allows modification of an enabled state.
 *
 * @author linus
 * @since 03/20/2023
 */
public interface IToggleable
{

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
