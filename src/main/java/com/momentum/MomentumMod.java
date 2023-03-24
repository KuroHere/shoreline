package com.momentum;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author linus
 * @since 01/09/2023
 */
@Mod(
        modid = MomentumMod.modId,
        name = MomentumMod.modName,
        version = MomentumMod.modVer,
        acceptedMinecraftVersions = "[" + MomentumMod.modMcVer + "]"
)
public class MomentumMod {

    // mod instance
    @Instance
    public MomentumMod instance;

    // mod identifier
    public static final String modId = "momentum";

    // mod name
    public static final String modName = "Momentum";

    // mod version
    // UPDATE BEFORE RELEASE
    public static final String modVer = "1.0";

    // mod mc version
    public static final String modMcVer = "1.12.2";

    /**
     *
     * @param event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        Momentum.preInit();
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {

        Momentum.init();
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        Momentum.postInit();
    }
}
