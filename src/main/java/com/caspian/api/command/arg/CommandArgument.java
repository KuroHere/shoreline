package com.caspian.api.command.arg;

import com.caspian.api.command.Command;
import com.caspian.init.Managers;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Command
 */
public class CommandArgument extends Argument<Command>
{
    //
    Collection<String> commandIds = new ArrayList<>();

    /**
     *
     */
    public CommandArgument()
    {
        for (Command command : Managers.COMMAND.getCommands())
        {
            commandIds.add(command.getName());
        }
    }

    /**
     * @see Command#runCommand()
     */
    @Override
    public void buildArgument()
    {
        for (Command command : Managers.COMMAND.getCommands())
        {
            if (command.getName().equalsIgnoreCase(getLiteral()))
            {
                setValue(command);
                break;
            }
        }
    }

    @Override
    public String[] getSuggestions()
    {
        return (String[]) commandIds.toArray();
    }
}
