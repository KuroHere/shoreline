package com.caspian.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric {@link ModInitializer}.
 *
 * @author linus
 * @since 1.0
 */
public class CaspianMod implements ClientModInitializer
{
    // Mod identifier
    public static final String MOD_ID = "caspian";
    // Mod name
    public static final String MOD_NAME = "Caspian";
    // Mod version - should comply with https://semver.org/
    // UPDATE BEFORE RELEASE
    public static final String MOD_VER = "1.0.0";
    // Build number - updated every time a major change happens
    // This is more of debug information and could be formatted as:
    // Caspian 1.0.{BUILD_NUMBER}-{GIT_HASH}
    public static final int BUILD_NUMBER = 1;
    // Mod mc version
    public static final String MOD_MC_VER = "1.19.4";

    /**
     * This code runs as soon as Minecraft is in a mod-load-ready state.
     * However, some things (like resources) may still be uninitialized.
     * Proceed with mild caution.
     */
    @Override
    public void onInitializeClient()
    {
         Caspian.preInit();
         Caspian.init();
         Caspian.postInit();
    }
}
