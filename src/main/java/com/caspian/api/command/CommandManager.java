package com.caspian.api.command;

import com.caspian.Caspian;
import com.caspian.api.command.Command;
import com.caspian.api.command.CommandHandler;
import com.caspian.impl.command.HelpCommand;

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
        Caspian.EVENT_HANDLER.subscribe(CommandHandler.class);
        register(
                new HelpCommand()
        );
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
    public Collection<Command> getCommands()
    {
        return commands;
    }
}
