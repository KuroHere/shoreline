package com.caspian.client.impl.command;

import com.caspian.client.Caspian;
import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.arguments.StringArgument;
import com.caspian.client.util.chat.ChatUtil;

import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ConfigCommand extends Command
{
    //
    Argument<String> actionArgument = new StringArgument("Action", "Whether " +
            "to save or load a preset", List.of("save", "load"));
    Argument<String> nameArgument = new StringArgument("ConfigName", "The " +
            "name for the config preset");

    /**
     *
     */
    public ConfigCommand()
    {
        super("Config", "Creates a new configuration preset");
    }

    /**
     * Runs when the command is inputted in chat
     */
    @Override
    public void onCommandInput()
    {
        String action = actionArgument.getValue();
        String name = nameArgument.getValue();
        if (action == null || name == null)
        {
            return;
        }
        if (action.equalsIgnoreCase("save"))
        {
            Caspian.CONFIG.setConfigPreset(name);

        }
        else if (action.equalsIgnoreCase("load"))
        {

        }
    }
}
