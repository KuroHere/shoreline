package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.command.Command;
import com.caspian.client.api.handler.CommandHandler;
import com.caspian.client.impl.command.HelpCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Command
 */
public class CommandManager
{
    //
    private final List<Command> commands = new ArrayList<>();

    /**
     *
     */
    public CommandManager()
    {
        // Caspian.EVENT_HANDLER.subscribe(this);
    }

    /**
     * Registers commands to the CommandManager
     */
    public void postInit()
    {
        register(
                new HelpCommand()
        );
        Caspian.info("Registered {} commands!", commands.size());
    }

    /**
     *
     *
     * @param commands
     */
    private void register(Command... commands)
    {
        for (Command command : commands)
        {
            register(command);
        }
    }

    /**
     *
     *
     * @param command
     */
    private void register(Command command)
    {
        commands.add(command);
    }

    /**
     *
     *
     * @return
     */
    public List<Command> getCommands()
    {
        return commands;
    }
}
