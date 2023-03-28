package com.momentum.init;

import com.momentum.impl.handler.ModuleHandler;

public class Handlers
{
    // handler instances
    public static ModuleHandler MODULE = new ModuleHandler();

    /**
     * Initializes all handlers
     */
    public static void init()
    {
        MODULE = new ModuleHandler();
    }
}
