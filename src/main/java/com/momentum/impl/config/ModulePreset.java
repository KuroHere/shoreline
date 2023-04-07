package com.momentum.impl.config;

import com.google.gson.JsonObject;
import com.momentum.api.config.file.IConfigFile;
import com.momentum.api.module.Module;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 *
 * @author linus
 * @since 03/29/2023
 *
 * @see ModuleConfigFile
 */
public class ModulePreset implements IConfigFile
{
    // module
    private final Module module;

    // preset path
    private final Path curr;

    /**
     * Initializes the preset with a file {@link Path}
     *
     * @param curr The file path
     */
    public ModulePreset(Module module, Path curr)
    {
        this.module = module;
        this.curr = curr;
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
     * @param p The path
     */
    @Override
    public void save(Path p)
    {
        // module file path
        Path path = p.resolve(module.getLabel() + ".json");

        // catches I0Exception
        try
        {
            // check if it already exists
            if (!Files.exists(path))
            {
                Files.createFile(path);
            }

            // module json object
            JsonObject json = module.toJson();
            String jsonString = gson.toJson(parser.parse(json.toString()));

            // output
            OutputStream out = Files.newOutputStream(path);

            // write our bytes to the output stream
            out.write(jsonString.getBytes(StandardCharsets.UTF_8), 0,
                    jsonString.length());
        }

        // error writing file
        catch (IOException e)
        {
            e.printStackTrace();
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
     * @param p The path
     */
    @Override
    public void load(Path p)
    {
        // module file path
        Path path = p.resolve(module.getLabel() + ".json");

        // check if file exists
        if (Files.exists(path))
        {
            // catches IOException
            try
            {
                // module json object
                InputStream inputStream = Files.newInputStream(path);
                JsonObject json =
                        parser.parse(new InputStreamReader(inputStream)).getAsJsonObject();

                // parse json
                module.fromJson(json);
            }

            // error writing file
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the preset {@link Path}
     *
     * @return The preset path
     */
    public Path getPath()
    {
        return curr;
    }
}
