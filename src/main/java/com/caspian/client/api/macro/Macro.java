package com.caspian.client.api.macro;

import org.lwjgl.glfw.GLFW;

/**
 *
 *
 * @param macro   Runnable macro which represents the functionality of the keybind.
 *                This code block will run when the key is pressed.
 * @param keycode The GLFW keycode which represents the macro keybind.
 *
 * @author linus
 * @since 1.0
 */
public record Macro(String name, int keycode, Runnable macro)
{
    /**
     *
     *
     * @see Runnable#run()
     */
    public void runMacro()
    {
        macro.run();
    }

    /**
     * Returns the macro keybind represented as {@link GLFW} keycode integer
     * between <b>0 and 348</b>.
     *
     * @return The macro keycode
     * @see #keycode
     */
    @Override
    public int keycode()
    {
        return keycode;
    }

    /**
     *
     *
     * @return
     */
    public String getId()
    {
        return String.format("%s-macro", name.toLowerCase());
    }

    /**
     * Returns the name associated with the macro keycode. Equivalent to
     * {@link GLFW#glfwGetKeyName(int, int)} on {@link #keycode}.
     *
     * @return The macro key name
     * @see #keycode
     * @see GLFW#glfwGetKeyScancode(int)
     */
    public String getKeyName()
    {
        return GLFW.glfwGetKeyName(keycode, GLFW.glfwGetKeyScancode(keycode));
    }
}
