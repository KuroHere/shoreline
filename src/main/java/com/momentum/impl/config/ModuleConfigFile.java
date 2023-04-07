package com.momentum.impl.config;

import com.momentum.api.config.file.ConfigFile;
import com.momentum.api.module.Module;
import com.momentum.init.Handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Config file directory for saving and loading {@link ModulePreset}
 *
 * @author linus
 * @since 03/29/2023
 */
public class ModuleConfigFile extends ConfigFile<Module>
{
    // module presets
    private Path curr;
    private final Set<ModulePreset> presets = new HashSet<>();

    /**
     * Creates a module config file
     */
    public ModuleConfigFile()
    {
        super();
        curr = dir.resolve("Modules");

        // catches IOException
        try
        {
            // create the module config directory if it doesn't already exist
            if (!Files.exists(curr) || !Files.isDirectory(curr))
            {
                Files.createDirectory(curr);
            }
        }

        // file write exception
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // create default presets
        for (Module mod : Handlers.MODULE.getModules())
        {
            presets.add(new ModulePreset(mod, curr));
        }
    }

    /**
     * Saves all configurations to current file
     */
    @Override
    public void save()
    {
        save(curr);
    }

    /**
     * Saves to a {@link Path}
     *
     * @param in The path
     */
    @Override
    public void save(Path in)
    {
        // save all presets in param path
        for (ModulePreset preset :
                presets.stream().filter(p -> p.getPath() == in).toList())
        {
            preset.save();
        }
    }

    /**
     * Loads all configurations from the current file
     */
    @Override
    public void load()
    {
        load(curr);
    }

    /**
     * Loads a {@link Path}
     *
     * @param in The path
     */
    @Override
    public void load(Path in)
    {
        // load all presets in param path
        for (ModulePreset preset :
                presets.stream().filter(p -> p.getPath() == in).toList())
        {
            preset.load();
        }
    }

    /**
     * Sets the current preset path
     *
     * @param in The path
     */
    public void setPreset(Path in)
    {
        // set curr path to param path if found in list of presets
        if (presets.stream().anyMatch(p -> p.getPath() == in))
        {
            curr = in;
        }

        // new path given, need to create a directory
        else
        {
            // catches I0Exception
            try
            {
                // create preset directory
                Files.createDirectory(in);
            }

            // error writing file
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // set as curr path and add to list of preset paths
            curr = in;
            addPreset(in);
        }
    }

    /**
     * Adds a preset {@link Path}
     *
     * @param p The file path
     */
    public void addPreset(Path p)
    {
        // create presets
        for (Module mod : Handlers.MODULE.getModules())
        {
            presets.add(new ModulePreset(mod, p));
        }
    }
}
