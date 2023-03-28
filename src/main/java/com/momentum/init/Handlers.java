package com.momentum.init;

import com.momentum.impl.handler.MacroHandler;
import com.momentum.impl.handler.ModuleHandler;

public class Handlers
{
    // handler instances
    public static ModuleHandler MODULE;
    public static MacroHandler MACRO;

    /**
     * Initializes all handlers
     */
    public static void init()
    {
        MODULE = new ModuleHandler();
        MACRO = new MacroHandler();
    }
}
