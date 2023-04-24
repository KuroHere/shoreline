package com.caspian;

import net.fabricmc.api.ModInitializer;

/**
 * Fabric {@link ModInitializer}.
 *
 * @author linus
 * @since 1.0
 */
public class CaspianMod implements ModInitializer
{
    // mod identifier
    public static final String MOD_ID = "caspian";

    // mod name
    public static final String MOD_NAME = "Caspian";

    // mod version
    // UPDATE BEFORE RELEASE
    public static final String MOD_VER = "1.0";

    // mod mc version
    public static final String MOD_MC_VER = "1.19.4";

    /**
     * Called when the fabric mod initializes
     */
    @Override
    public void onInitialize()
    {
        Caspian.preInit();
        Caspian.init();
        Caspian.postInit();
    }
}
