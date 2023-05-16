package com.caspian.api.file;

import com.caspian.Caspian;
import com.caspian.api.module.ModulePreset;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see ModulePreset
 */
public abstract class ConfigFile
{
    //
    protected static final Gson GSON = new GsonBuilder()
            .setLenient() // leniency to allow for .cfg files
            .setPrettyPrinting()
            .create();
    //
    protected static final JsonParser PARSER = new JsonParser();
    // The UNIX filepath to configuration file. This filepath is always
    // within the client directory.
    private final Path filepath;

    /**
     *
     *
     * @param path
     */
    public ConfigFile(Path dir, String path)
    {
        // create directory
        if (!Files.exists(dir))
        {
            try
            {
                Files.createDirectory(dir);
            }
            // create dir error
            catch (IOException e)
            {
                Caspian.error("Could not create %s dir", dir);
                e.printStackTrace();
            }
        }
        filepath = dir.resolve(path);
    }

    /**
     * Returns the <tt>UNIX</tt> {@link Path} to configuration file in the
     * client directory.
     *
     * @return The path to the file
     *
     * @see #filepath
     */
    public Path getFilepath()
    {
        return filepath;
    }

    /**
     *
     */
    public abstract void save();

    /**
     *
     */
    public abstract void load();
}
