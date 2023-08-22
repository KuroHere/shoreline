package com.caspian.client.impl.command;

import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.arguments.CommandArgument;
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
    //
    Argument<Command> commandArg = new CommandArgument("HelpCommand", "The " +
            "specified command to display info");

    /**
     *
     */
    public HelpCommand()
    {
        super("help", "<opt:command>", "Displays command functionality");
    }

    /**
     *
     */
    @Override
    public void onCommandInput()
    {
        final Command command = commandArg.parse();
        if (command != null)
        {
            if (isModuleCommand(command))
            {
                ChatUtil.clientSendMessage("module <setting> <value> - Configures the module");
                return;
            }
            ChatUtil.clientSendMessage(toHelpMessage(command));
        }
        else
        {
            boolean sent = false;
            for (Command c : Managers.COMMAND.getCommands())
            {
                if (isModuleCommand(c) && !sent)
                {
                    ChatUtil.clientSendMessage("module <setting> <value> - Configures the module");
                    sent = true;
                    continue;
                }
                ChatUtil.clientSendMessage(toHelpMessage(c));
            }
        }
    }

    /**
     *
     * @param command
     * @return
     */
    private String toHelpMessage(Command command)
    {
        return String.format("%s %s - %s", command.getName(),
                command.getUsage(), command.getDescription());
    }

    private boolean isModuleCommand(Command command)
    {
        // DO NOT LOOK AT THIS BS
        return command.getUsage().equals("<setting> <value>");
    }
}
