package com.caspian;

import com.caspian.api.event.handler.EventHandler;
import com.caspian.api.file.ClientConfiguration;
import com.caspian.init.Managers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Client main class. Handles main client mod initializing of static service
 * instances and client managers.
 *
 * @author linus
 * @since 1.0
 *
 * @see CaspianMod
 */
public class Caspian
{
    // Client logger.
    public static Logger LOGGER;

    // Client Event handler (aka Event bus) which handles event dispatching
    // and listening for client events.
    public static EventHandler EVENT_HANDLER;

    // Client configuration handler. This master saves/loads the client
    // configuration files which have been saved locally.
    public static ClientConfiguration CONFIG;

    // Client shutdown hooks which will run once when the MinecraftClient
    // game instance is shutdown.
    public static ShutdownHook SHUTDOWN;

    /**
     * Called before {@link CaspianMod#onInitialize()}
     */
    public static void preInit()
    {
        LOGGER = LogManager.getLogger("Caspian");
        info("Starting preInit ...");
        EVENT_HANDLER = new EventHandler();
        Managers.init();
    }

    /**
     * Called during {@link CaspianMod#onInitialize()}
     */
    public static void init()
    {
        info("Starting init ...");
        CONFIG = new ClientConfiguration();
        CONFIG.loadClient();
    }

    /**
     * Called after {@link CaspianMod#onInitialize()}
     */
    public static void postInit()
    {
        info("Starting postInit ...");
        SHUTDOWN = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(SHUTDOWN);
    }

    /**
     * Wrapper method for {@link Logger#info(String)} which logs a message to
     * the client {@link Logger}.
     *
     * @param message The log message
     *
     * @see Logger#info(String)
     */
    public static void info(String message)
    {
        LOGGER.info(message);
    }

    /**
     * Wrapper method for {@link Logger#error(String)} which logs an error to
     * the client {@link Logger}.
     *
     * @param message The log message
     *
     * @see Logger#error(String)
     */
    public static void error(String message)
    {
        LOGGER.error(message);
    }
}
