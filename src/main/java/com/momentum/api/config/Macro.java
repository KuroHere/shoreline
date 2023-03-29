package com.momentum.api.config;

import org.lwjgl.glfw.GLFW;

/**
 * Macro general implementation associated with a LWJL keycode
 * and a {@link Runnable}
 *
 * @author linus
 * @since 03/20/2023
 */
public class Macro
{
    // macro key
    // preform action on press
    private final Integer keycode;

    // runnable macro action
    // macro will typically invoke this on key press
    private final Runnable action;

    /**
     * Initializes the macro with a key
     *
     * @param keycode The macro key
     * @param action The macro action
     * @throws IllegalArgumentException if keycode is not a valid LWJGL keycode
     */
    public Macro(Integer keycode, Runnable action)
    {
        // check keycode is valid
        if (keycode < 0 || keycode > 255)
        {
            throw new IllegalArgumentException(
                    "keycode is not a valid LWJGL keycode");
        }

        // init
        this.keycode = keycode;
        this.action = action;
    }

    /**
     * Called when the macro key is pressed
     */
    public void invoke()
    {
        // run action runnable
        action.run();
    }

    /**
     * Gets the macro key name
     *
     * @return The macro key name
     */
    public String getKeyName()
    {
        // keyboard key name
        int scancode = GLFW.glfwGetKeyScancode(keycode);
        return GLFW.glfwGetKeyName(keycode, scancode);
    }

    /**
     * Gets the macro key code
     *
     * @return The macro key code
     */
    public int getKeycode()
    {
        return keycode;
    }

    /**
     * Gets the macro action
     *
     * @return The macro action
     */
    public Runnable getAction()
    {
        return action;
    }
}
