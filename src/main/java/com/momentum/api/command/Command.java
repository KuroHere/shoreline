package com.momentum.api.command;

import com.momentum.api.command.argument.Argument;
import com.momentum.api.registry.ILabeled;
import com.momentum.api.util.Globals;

/**
 * Client command in the {@link net.minecraft.client.gui.hud.ChatHud} that
 * can take a set of {@link Argument}.
 *
 * @author linus
 * @since 1.0
 *
 * @see Argument
 * @see CommandDispatcher
 */
public abstract class Command implements Globals, ILabeled
{
    // the unique command identifier, used to identify the command in the chat
    // Ex: help, prefix, openfolder, ...
    // private final String usage;

    // The command arguments, For reference:
    // If the command line (chat) is split up by the " " then the command
    // .help help_command would be split into args -> "help" and "help_command"
    // {@see Argument}
    private final Argument[] args;

    /**
     * Initializes the command identifier and expected arguments
     *
     * @param args The expected argument types
     */
    public Command(Argument... args)
    {
        this.args = args;
    }

    /**
     * Indicates whether some other {@link Command} is "equal" to this one.
     * Commands can be compared to {@link String} which will compare the
     * command id.
     *
     * @return <tt>true</tt> if the obj is the same as this command
     */
    @Override
    public boolean equals(Object obj)
    {
        // check command id
        if (obj instanceof String)
        {
            return args[0].getArg().equalsIgnoreCase((String) obj);
        }

        // check obj
        return super.equals(obj);
    }

    /**
     * Tab completes the current {@link Argument} and returns the completed
     * argument. Runs {@link Argument#tabComplete()}.
     */
    public void tabComplete()
    {
        for (Argument<?> argument : args)
        {
            // tab complete first non-null arg
            if (argument.getArg() != null)
            {
                argument.tabComplete();
                return;
            }
        }
    }

    /**
     * Returns the command usage
     *
     * @return The command usage
     */
    public String getUsage()
    {
        return args[0].getArg();
    }

    /**
     * Returns the label for the {@link Command} which is the unique command
     * identifier with the type "command"
     *
     * @return The command label
     */
    @Override
    public String getLabel()
    {
        return args[0].getArg() + "_command";
    }

    /**
     * Sets the argument values based on the String literal in the chat
     *
     * @param in The string literal args
     */
    public void setArgValues(String[] in)
    {
        // update all args
        for (int i = 0; i < args.length; i++)
        {
            args[i].setArgValue(in[i]);
        }
    }

    /**
     * Returns the {@link Argument} structure for the command
     *
     * @return The arg structure
     */
    public Argument[] getArgs()
    {
        return args;
    }

    /**
     * Returns the Length of command {@link Argument} structure
     *
     * @return Length of command arg structure
     */
    public int getArgsLength()
    {
        return args.length;
    }

    /**
     * Returns the {@link Argument} at a param index in the command
     * arg structure
     *
     * @param idx The index
     * @return The arg at the param index
     */
    public Argument<?> getArg(int idx)
    {
        return args[idx];
    }

    /**
     * Invokes the command with a collection of param {@link Argument}
     */
    public abstract void invoke();
}
