package com.caspian.init;

import com.caspian.Caspian;
import com.caspian.api.account.AccountManager;
import com.caspian.api.command.CommandManager;
import com.caspian.api.macro.MacroManager;
import com.caspian.api.module.ModuleManager;
import com.caspian.api.social.SocialManager;
import com.caspian.api.waypoint.WaypointManager;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 *
 */
public class Managers
{
    // The initialized state of the managers. Once this is true, all managers
    // have been initialized and the init process is complete. As a general
    // rule, it is good practice to check this state before accessing instances.
    private static boolean initialized;

    // Manager instances. Managers can be statically referenced after
    // initialized. Managers will be initialized in this order.
    public static ModuleManager MODULE;
    public static MacroManager MACRO;
    public static CommandManager COMMAND;
    public static SocialManager SOCIAL;
    public static WaypointManager WAYPOINT;
    public static AccountManager ACCOUNT;

    /**
     * Initializes the manager instances. Should not be used if the
     * managers are already initialized.
     *
     * @see #isInitialized()
     */
    public static void init()
    {
        if (!isInitialized())
        {
            MODULE = new ModuleManager();
            COMMAND = new CommandManager();
            MACRO = new MacroManager();
            SOCIAL = new SocialManager();
            WAYPOINT = new WaypointManager();
            ACCOUNT = new AccountManager();
            initialized = true;
        }
    }

    /**
     * Returns <tt>true</tt> if the manager instances have been initialized.
     * This should always return <tt>true</tt> if {@link Caspian#preInit()} has
     * finished running.
     *
     * @return <tt>true</tt> if the manager instances have been initialized
     *
     * @see #init()
     * @see #initialized
     */
    public static boolean isInitialized()
    {
        return initialized;
    }
}
