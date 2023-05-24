package com.caspian.client.api.command;

import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.util.Globals;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class Command implements Globals
{
    // the unique command identifier, used to identify the command in the chat.
    // Ex: help, prefix, openfolder, etc.
    private final String name;

    //
    private final String usage;

    //
    private final String desc;

    // The command arguments, For reference: If the command line (chat) is
    // split up by the " " then the command ".friend add {player_name}" would
    // be split into args -> "add" and "{player_name}"
    private final ArrayList<Argument<?>> struct;

    /**
     *
     *
     * @param name
     * @param usage
     * @param desc
     * @param struct
     */
    public Command(String name, String usage, String desc, Argument<?>... struct)
    {
        this.name = name;
        this.usage = usage;
        this.desc = desc;
        this.struct = new ArrayList<>(Arrays.asList(struct));
    }

    /**
     *
     */
    public void runCommand()
    {
        for (Argument<?> arg : struct)
        {
            arg.buildArgument();
        }

        // impl
    }

    /**
     *
     *
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * 
     * 
     * @return
     */
    public String getUsage()
    {
        return usage;
    }

    /**
     * 
     * 
     * @return
     */
    public String getDescription()
    {
        return desc;
    }

    /**
     * Returns the {@link Argument} at the param index in the command 
     * argument structure.
     * 
     * @param i The index of the arg
     * @return Returns the arg at the param index
     */
    public Argument<?> getArg(int i)
    {
        return struct.get(i);
    }

    /**
     *
     *
     * @param literals
     *
     * @see Argument#setLiteral(String)
     */
    public void setArgInputs(String[] literals)
    {
        int i = 0;
        for (Argument<?> arg : struct)
        {
            arg.setLiteral(literals[i]);
            i++;
        }
    }
}
