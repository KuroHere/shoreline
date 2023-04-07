package com.momentum;

import net.fabricmc.api.ModInitializer;

/**
 * @author linus
 * @since 01/09/2023
 */
public class MomentumMod implements ModInitializer
{
    // mod identifier
    public static final String MOD_ID = "momentum";

    // mod name
    public static final String MOD_NAME = "Momentum";

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
        Momentum.preInit();
        Momentum.init();
        Momentum.postInit();
    }
}
