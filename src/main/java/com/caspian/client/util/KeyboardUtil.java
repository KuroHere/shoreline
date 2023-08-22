package com.caspian.client.util;

import org.lwjgl.glfw.GLFW;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class KeyboardUtil
{
    /**
     *
     * @param key
     * @return
     */
    public static int getKeyCode(String key)
    {
        if (key.equalsIgnoreCase("NONE"))
        {
            return GLFW.GLFW_KEY_UNKNOWN;
        }
        for (int i = 39; i < 97; i++)
        {
            if (key.equalsIgnoreCase(GLFW.glfwGetKeyName(i,
                    GLFW.glfwGetKeyScancode(i))))
            {
                return i;
            }
        }
        return GLFW.GLFW_KEY_UNKNOWN;
    }
}
