package com.momentum.impl.command;

import com.momentum.Momentum;
import com.momentum.api.command.Command;
import com.momentum.api.command.argument.Argument;
import com.momentum.api.command.argument.StringArgument;
import com.momentum.api.util.chat.ChatUtil;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class HelpCommand extends Command
{
    /**
     * Initializes a new help command
     */
    public HelpCommand()
    {
        // help - Displays a list of all available commands
        super(new StringArgument("help"), new StringArgument(
                "<opt:command>", Momentum.CMD_DISPATCHER.getCommandUsages()));
    }

    /**
     * Invokes the command with a collection of param {@link Argument}
     */
    @Override
    public void invoke()
    {
        // optional command arg
        StringArgument commandArg = (StringArgument) getArg(1);

        // arg was passed
        if (commandArg != null)
        {
            // show usage of specified command
            for (Command command : Momentum.CMD_DISPATCHER.getCommands())
            {
                // matches arg
                if (command.equals(commandArg.getArgValue()))
                {
                    ChatUtil.clientSendMessage(command.getUsage());
                    break;
                }
            }
        }

        else
        {
            // show usage of all commands
            for (Command command : Momentum.CMD_DISPATCHER.getCommands())
            {
                ChatUtil.clientSendMessage(command.getUsage());
            }
        }
    }
}
