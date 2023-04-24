package com.caspian.api.file;

import com.caspian.Caspian;
import com.caspian.api.account.AccountPreset;
import com.caspian.api.account.AccountType;
import com.caspian.api.module.Module;
import com.caspian.api.module.ModulePreset;
import com.caspian.api.social.Relation;
import com.caspian.api.social.SocialPreset;
import com.caspian.api.waypoint.WaypointPreset;
import com.caspian.init.Managers;
import com.caspian.util.Globals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 * 
 * @see ConfigFile
 */
public class ClientConfiguration implements Globals
{
    // Main client directory. This folder will contain all locally saved 
    // configurations for the client.
    private Path clientDir;

    // Set of configuration files that must be saved and loaded. This can be
    // modified after init.
    private final Set<ConfigFile> files = new HashSet<>();

    /**
     * 
     */
    public ClientConfiguration() 
    {
        final Path runningDir = mc.runDirectory.toPath();
        try
        {
            File homeDir = new File(System.getProperty("user.home"));
            clientDir = homeDir.toPath();
        }

        // will resort to running dir if client does not have access to the 
        // user home dir
        catch (Exception e)
        {
            Caspian.error("Could not access home dir, defaulting to running dir");
            e.printStackTrace();
            clientDir = runningDir;
        }

        finally
        {
            // cannot write, minecraft always has access to the running dir
            if (clientDir == null || !Files.exists(clientDir)
                    || !Files.isWritable(clientDir))
            {
                clientDir = runningDir;
            }

            clientDir = clientDir.resolve("Caspian");

            // create client directory
            if (!Files.exists(clientDir))
            {
                try
                {
                    Files.createDirectory(clientDir);
                }

                // write error
                catch (IOException e)
                {
                    Caspian.error("Could not create client dir");
                    e.printStackTrace();
                }
            }
        }

        for (Module module : Managers.MODULE.getModules())
        {
            // files.add(new ModulePreset(clientDir.resolve("Defaults"), module));
            files.add(new ModulePreset(clientDir.resolve("Modules"), module));
        }
        for (Relation relation : Relation.values())
        {
            files.add(new SocialPreset(clientDir, relation));
        }
        for (String ip : Managers.WAYPOINT.getIps())
        {
            files.add(new WaypointPreset(clientDir.resolve("Waypoints"), ip));
        }
        for (AccountType accountType : AccountType.values())
        {
            files.add(new AccountPreset(clientDir.resolve("Accounts"),
                    accountType));
        }
    }
    
    /**
     *
     */
    public void saveClient()
    {
        for (ConfigFile file : files)
        {
            file.save();
        }
    }

    /**
     *
     */
    public void loadClient()
    {
        for (ConfigFile file : files)
        {
            file.load();
        }
    }
}
