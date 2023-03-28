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
        modid = MomentumMod.MOD_ID,
        name = MomentumMod.MOD_NAME,
        version = MomentumMod.MOD_VER,
        acceptedMinecraftVersions = "[" + MomentumMod.MOD_MC_VER + "]"
)
public class MomentumMod
{
    // mod instance
    @Instance
    public static MomentumMod INSTANCE;

    // mod identifier
    public static final String MOD_ID = "momentum";

    // mod name
    public static final String MOD_NAME = "Momentum";

    // mod version
    // UPDATE BEFORE RELEASE
    public static final String MOD_VER = "1.0";

    // mod mc version
    public static final String MOD_MC_VER = "1.12.2";

    /**
     *
     * @param event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Momentum.preInit();
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Momentum.init();
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        Momentum.postInit();
    }
}
