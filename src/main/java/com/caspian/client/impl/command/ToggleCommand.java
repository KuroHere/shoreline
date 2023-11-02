package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.arguments.ModuleArgument;
import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.util.chat.ChatUtil;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ToggleCommand extends Command
{
    //
    Argument<Module> moduleArgument = new ModuleArgument("Module", "The " +
            "module to enable/disable");

    /**
     *
     */
    public ToggleCommand()
    {
        super("Toggle", "Enables/Disables a module");
    }

    /**
     * Runs when the command is inputted in chat
     */
    @Override
    public void onCommandInput()
    {
        Module module = moduleArgument.getValue();
        if (module instanceof ToggleModule t)
        {
            t.toggle();
            ChatUtil.clientSendMessage("%s is now %s!", t.getName(),
                    t.isEnabled() ? "§aenabled" : "§cdisabled");
        }
    }
}
