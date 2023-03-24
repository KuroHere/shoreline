package com.momentum;

import com.momentum.api.event.handler.EventHandler;

/**
 * Client main
 *
 * @author linus
 * @since 01/09/2023
 */
public class Momentum {

    // event bus
    public static EventHandler EVENT_HANDLER;

    /**
     * Runs
     * during {@link net.minecraftforge.fml.common.event.FMLPreInitializationEvent}
     */
    public static void preInit() {

        // init event bus
        EVENT_HANDLER = new EventHandler();
    }

    /**
     * Runs during
     * {@link net.minecraftforge.fml.common.event.FMLInitializationEvent}
     */
    public static void init() {

    }

    /**
     * Runs
     * during {@link net.minecraftforge.fml.common.event.FMLPostInitializationEvent}
     */
    public static void postInit() {

    }
}
