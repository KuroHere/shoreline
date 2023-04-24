package com.caspian.api.command.arg;

import com.caspian.api.command.Command;
import com.caspian.api.module.Module;
import com.caspian.init.Managers;

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
    private final Collection<String> moduleNames = new ArrayList<>();

    /**
     *
     */
    public ModuleArgument()
    {
        for (Module module : Managers.MODULE.getModules())
        {
            moduleNames.add(module.getName());
        }
    }

    /**
     *
     *
     * @see Command#runCommand()
     */
    @Override
    public void buildArgument()
    {
        for (Module module : Managers.MODULE.getModules())
        {
            if (module.getName().equalsIgnoreCase(getLiteral()))
            {
                setValue(module);
                break;
            }
        }
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String[] getSuggestions()
    {
        return (String[]) moduleNames.toArray();
    }
}
