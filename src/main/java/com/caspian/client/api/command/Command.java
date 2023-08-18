package com.caspian.client.api.command;

import com.caspian.client.Caspian;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.ArgumentFactory;
import com.caspian.client.api.config.ConfigContainer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public abstract class Command extends ConfigContainer
{
    // The unique command identifier, used to identify the command in the chat.
    // Ex: help, prefix, openfolder, etc.
    private final String usage;
    //
    private final String desc;
    // The command arguments, For reference: If the command line (chat) is
    // split up by the " " then the command ".friend add {player_name}" would
    // be split into args -> "add" and "{player_name}"
    private final List<Argument<?>> arguments = new ArrayList<>();

    /**
     *
     *
     * @param name
     * @param usage
     * @param desc
     */
    public Command(String name, String usage, String desc)
    {
        super(name);
        this.usage = usage;
        this.desc = desc;
    }

    /**
     *
     * @param arg
     */
    protected void register(Argument<?> arg)
    {
        arguments.add(arg);
    }

    /**
     *
     * @param args
     */
    protected void register(Argument<?>... args)
    {
        arguments.addAll(Arrays.asList(args));
    }

    /**
     *
     */
    @Override
    public void reflectConfigs()
    {
        final ArgumentFactory factory = new ArgumentFactory(this);
        // populate container using reflection
        for (Field field : getClass().getDeclaredFields())
        {
            if (Argument.class.isAssignableFrom(field.getType()))
            {
                Argument<?> argument = factory.build(field);
                if (argument == null)
                {
                    // failsafe for debugging purposes
                    Caspian.error("Value for field {} is null!", field);
                    continue;
                }
                register(argument);
            }
        }
    }

    /**
     * Runs when the command is inputted in chat
     */
    public abstract void onCommandInput();

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
        return arguments.get(i);
    }

    /**
     *
     * @return
     */
    public List<Argument<?>> getArgs()
    {
        return arguments;
    }
}
