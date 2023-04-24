package com.caspian.util;

import net.minecraft.client.MinecraftClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public interface Globals
{
    // Minecraft game instance
    MinecraftClient mc = MinecraftClient.getInstance();

    //
    ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
}
