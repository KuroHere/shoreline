package com.momentum;

<<<<<<< Updated upstream
import com.momentum.api.event.EventBus;
import com.momentum.impl.handlers.NcpHandler;
import com.momentum.impl.managers.ChatManager;
import com.momentum.impl.handlers.CommandHandler;
import com.momentum.impl.handlers.ModuleHandler;
import com.momentum.impl.registers.CommandRegistry;
import com.momentum.impl.registers.ConfigRegistry;
import com.momentum.impl.registers.ModuleRegistry;
import com.momentum.impl.init.Configs;
import com.momentum.impl.handlers.TickHandler;
import com.momentum.impl.ui.ClickGuiScreen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
=======
import com.momentum.api.event.handler.EventHandler;
>>>>>>> Stashed changes

/**
 * Client main
 *
 * @author linus
 * @since 01/09/2023
 */
public class Momentum {

    // event bus
    public static EventHandler EVENT_HANDLER;

<<<<<<< Updated upstream
    // registries
    public static ModuleRegistry MODULE_REGISTRY;
    public static CommandRegistry COMMAND_REGISTRY;
    public static ConfigRegistry CONFIG_REGISTRY;

    // handler
    public static ModuleHandler MODULE_HANDLER;
    public static CommandHandler COMMAND_HANDLER;
    public static NcpHandler NCP_HANDLER;
    public static TickHandler TICK_HANDLER;

    // managers
    public static ChatManager CHAT_MANAGER;

    // click gui
    public static ClickGuiScreen CLICK_GUI;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        // create event bus
        EVENT_BUS = new EventBus();
        // logger = event.getModLog();
=======
    /**
     * Runs
     * during {@link net.minecraftforge.fml.common.event.FMLPreInitializationEvent}
     */
    public static void preInit() {

        // init event bus
        EVENT_HANDLER = new EventHandler();
>>>>>>> Stashed changes
    }

    /**
     * Runs during
     * {@link net.minecraftforge.fml.common.event.FMLInitializationEvent}
     */
    public static void init() {

<<<<<<< Updated upstream
        // initialize registries
        MODULE_REGISTRY = new ModuleRegistry();
        COMMAND_REGISTRY = new CommandRegistry();
        CONFIG_REGISTRY = new ConfigRegistry();

        // initialize handlers
        MODULE_HANDLER = new ModuleHandler();
        COMMAND_HANDLER = new CommandHandler();
        NCP_HANDLER = new NcpHandler();
        TICK_HANDLER = new TickHandler();

        // initialize managers
        CHAT_MANAGER = new ChatManager();

        // initialize click gui
        CLICK_GUI = new ClickGuiScreen();

        // catches exception
        try {

            // attempt load
            Configs.DEFAULT_CONFIG.save();
            Configs.load();
        }

        // failed to load
        catch (Exception e) {
            e.printStackTrace();
        }
=======
    }

    /**
     * Runs
     * during {@link net.minecraftforge.fml.common.event.FMLPostInitializationEvent}
     */
    public static void postInit() {

>>>>>>> Stashed changes
    }
}
