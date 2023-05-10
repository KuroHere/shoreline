package com.caspian.impl.command;

import com.caspian.api.command.arg.CommandArgument;
import com.caspian.api.command.Command;
import com.caspian.init.Managers;
import com.caspian.util.chat.ChatUtil;

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
            ChatUtil.clientSendMessage(commandArg.getId() + " - " +
                    commandArg.getDescription());
        }

        else
        {
            for (Command command : Managers.COMMAND.getCommands())
            {
                ChatUtil.clientSendMessage(command.getId() + " - " +
                        command.getDescription());
            }
        }
    }
}
