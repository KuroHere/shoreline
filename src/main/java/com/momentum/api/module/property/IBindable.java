package com.momentum.api.module.property;

import com.momentum.api.config.Macro;

/**
 * Supports keybindings through the use of {@link Macro}.
 *
 * @author linus
 * @since 03/26/2023
 */
public interface IBindable
{
    /**
     * Binds the object to a keybinding
     *
     * @param key The bind keycode
     * @throws IllegalArgumentException if key is not a valid keycode on the
     * LWJGL keyboard
     */
    void bind(int key);

    /**
     * Binds the object to a macro
     *
     * @param m The bind macro
     * @throws IllegalArgumentException if key is not a valid keycode on the
     * LWJGL keyboard
     */
    void bind(Macro m);

    /**
     * Called when the object is bound to a key
     */
    void onBind();

    /**
     * Returns the bind macro
     *
     * @return The bind macro
     */
    Macro getKeybinding();
}
