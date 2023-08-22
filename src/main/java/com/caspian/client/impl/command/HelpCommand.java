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
    Argument<Command> commandArg = new CommandArgument("Command", "The " +
            "specified command to display info");

    /**
     *
     */
    public HelpCommand()
    {
        super("Help", "Displays command functionality");
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
            if (command instanceof ModuleCommand)
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
                ChatUtil.clientSendMessageRaw("§7[§fCommands Help§7]");
                if (c instanceof ModuleCommand)
                {
                    if (!sent)
                    {
                        ChatUtil.clientSendMessageRaw("module <setting> <value> - Configures the module");
                        sent = true;
                    }
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
        return String.format("%s %s- %s", command.getName(),
                command.getUsage(), command.getDescription());
    }
}
