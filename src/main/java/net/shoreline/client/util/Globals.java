package net.shoreline.client.util;

import net.minecraft.client.MinecraftClient;

/**
 * TERRIBLE CODING PRACTICE
 *
 * @author linus
 * @since 1.0
 */
public interface Globals
{
    // Minecraft game instance
    MinecraftClient mc = MinecraftClient.getInstance();
    default boolean isNull() {
        return mc.player == null || mc.world == null;
    }
}
