package com.caspian.client.api.command.arg.arguments;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.ArgumentParseException;
import com.caspian.client.api.module.Module;
import com.caspian.client.init.Managers;
import com.caspian.client.util.chat.ChatUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ModuleArgument extends Argument<Module>
{
    //
    private final ArrayList<String> moduleNames = new ArrayList<>();

    /**
     *
     *
     * @param name
     * @param desc
     */
    public ModuleArgument(String name, String desc)
    {
        super(name, desc);
        for (Module module : Managers.MODULE.getModules())
        {
            moduleNames.add(module.getName());
        }
    }

    /**
     * @see Command#onCommandInput()
     */
    @Override
    public Module parse() throws ArgumentParseException
    {
        for (Module module : Managers.MODULE.getModules())
        {
            if (module.getName().equalsIgnoreCase(getLiteral()))
            {
                return module;
            }
        }
        throw new ArgumentParseException("Could not parse Module argument!");
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Collection<String> getSuggestions()
    {
        return moduleNames;
    }
}
