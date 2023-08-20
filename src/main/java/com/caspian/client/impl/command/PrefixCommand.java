package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.arguments.StringArgument;
import com.caspian.client.init.Managers;
import com.caspian.client.util.chat.ChatUtil;
import net.minecraft.util.Formatting;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PrefixCommand extends Command
{
    //
    Argument<String> prefixArg = new StringArgument("Prefix", "The new " +
            "chat command prefix");

    /**
     *
     */
    public PrefixCommand()
    {
        super("prefix", "<single-char prefix>", "Allows you to change the " +
                "chat command prefix");
    }

    /**
     * Runs when the command is inputted in chat
     */
    @Override
    public void onCommandInput()
    {
        final String prefix = prefixArg.parse();
        if (prefix != null)
        {
            ChatUtil.clientSendMessage("Client command prefix changed to " +
                    Formatting.AQUA + prefix + Formatting.RESET + "!");
            Managers.COMMAND.setPrefix(prefix);
        }
    }
}
