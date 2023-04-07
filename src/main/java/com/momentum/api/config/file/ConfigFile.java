package com.momentum.api.config.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ConfigFile implementation used to save {@link com.momentum.api.config.Config}
 * to a <tt>.cfg</tt> file.
 *
 * @author linus
 * @since 03/20/2023
 *
 * @param <T> Type of config, can only save those of type {@link IConfigurable}
 */
public abstract class ConfigFile<T extends IConfigurable<?>>
        implements IConfigFile
{
    // curr working directory
    protected Path dir;

    /**
     * Creates a new config file with properties based on the
     * {@link IConfigurable} interface. Does not indicate when to save or load.
     */
    public ConfigFile()
    {
        // game running dir
        final Path running = Paths.get("");

        // catches Exception
        try
        {
            // user home dir
            String home = System.getProperty("user.home");
            File file = new File(home);

            // file dir is the home dir
            dir = file.toPath();
        }

        // cannot write
        catch (Exception e)
        {
            // current dir becomes running dir
            e.printStackTrace();
            dir = running;
        }

        // check running directory
        finally
        {
            // if we cannot write, minecraft always has access
            // to the running dir
            if (dir == null
                    || !Files.exists(dir)
                    || !Files.isWritable(dir))
            {
                // working dir becomes running dir
                dir = running;
            }

            // client dir
            dir = dir.resolve("Momentum");

            // check exists
            if (!Files.exists(dir))
            {
                // catches IOException
                try
                {
                    // create client dir
                    Files.createDirectory(dir);
                }

                // write error
                catch (IOException e)
                {
                    e.printStackTrace();
                    dir = running;
                }
            }
        }
    }

    /**
     * Saves all configurations to current file
     */
    @Override
    public abstract void save();

    /**
     * Saves to a {@link Path}
     *
     * @param p The path
     */
    @Override
    public abstract void save(Path p);

    /**
     * Loads all configurations from the current file
     */
    @Override
    public abstract void load();

    /**
     * Loads a {@link Path}
     *
     * @param p The path
     */
    @Override
    public abstract void load(Path p);
}
