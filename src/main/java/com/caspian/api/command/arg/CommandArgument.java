package com.caspian.api.command.arg;

import com.caspian.api.command.Command;
import com.caspian.init.Managers;

import java.util.ArrayList;
import java.util.Collection;

public class CommandArgument extends Argument<Command>
{
    Collection<String> commandIds = new ArrayList<>();

    public CommandArgument()
    {
        for (Command command : Managers.COMMAND.getCommands())
        {
            commandIds.add(command.getId());
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
            if (command.getId().equalsIgnoreCase(getLiteral()))
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
