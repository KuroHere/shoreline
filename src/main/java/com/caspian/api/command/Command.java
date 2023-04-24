package com.caspian.api.command;

import com.caspian.api.command.arg.Argument;
import com.caspian.util.Globals;

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
    private final String id;

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
     * @param id
     * @param usage
     * @param desc
     * @param struct
     */
    public Command(String id, String usage, String desc, Argument<?>... struct)
    {
        this.id = id;
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
    public String getId()
    {
        return id;
    }

    public String getUsage()
    {
        return usage;
    }

    public String getDescription()
    {
        return desc;
    }

    public Argument<?> getArg(int idx)
    {
        return struct.get(idx);
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
