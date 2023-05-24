package com.caspian.client.impl.command;

import com.caspian.client.api.command.arg.CommandArgument;
import com.caspian.client.api.command.Command;
import com.caspian.client.init.Managers;
import com.caspian.client.util.chat.ChatUtil;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class HelpCommand extends Command
{
    /**
     *
     */
    public HelpCommand()
    {
        super("help", "<opt:command>", "Displays command functionality",
                new CommandArgument());
    }

    /**
     *
     */
    @Override
    public void runCommand()
    {
        super.runCommand();
        Command commandArg = (Command) getArg(0).getValue();
        if (commandArg != null)
        {
            ChatUtil.clientSendMessage(commandArg.getName() + " - " +
                    commandArg.getDescription());
        }

        else
        {
            for (Command command : Managers.COMMAND.getCommands())
            {
                ChatUtil.clientSendMessage(command.getName() + " - " +
                        command.getDescription());
            }
        }
    }
}
