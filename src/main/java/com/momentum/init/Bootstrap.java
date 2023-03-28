package com.momentum.init;

/**
 * Client initializer bootstrap
 *
 * @author linus
 * @since 03/27/2023
 */
public class Bootstrap
{
    // initialization state
    private static boolean initialized;

    /**
     * Initializes the client bootstrap
     */
    public static void init()
    {
        // check already initialized
        if (!initialized)
        {
            Handlers.init();
            initialized = true;
        }
    }

    /**
     * Returns the initialization state
     *
     * @return The initialization state
     */
    public static boolean isInitialized()
    {
        return initialized;
    }
}
