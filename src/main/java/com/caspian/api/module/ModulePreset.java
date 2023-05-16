package com.caspian.api.module;

import com.caspian.Caspian;
import com.caspian.api.file.ConfigFile;
import com.google.gson.JsonObject;

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
 * @since 1.0
 *
 * @see Module
 * @see ConfigFile
 */
public class ModulePreset extends ConfigFile
{
    //
    private final Module module;

    /**
     *
     *
     * @param dir
     * @param module
     */
    public ModulePreset(Path dir, Module module)
    {
        super(dir, module.getId() + ".json");
        this.module = module;
    }

    /**
     *
     */
    @Override
    public void save()
    {
        Path filepath = getFilepath();
        try
        {
            if (!Files.exists(filepath))
            {
                Files.createFile(filepath);
            }

            JsonObject json = module.toJson();
            String jsonString = GSON.toJson(PARSER.parse(json.toString()));
            OutputStream out = Files.newOutputStream(filepath);
            out.write(jsonString.getBytes(StandardCharsets.UTF_8), 0,
                    jsonString.length());
        }

        // error writing file
        catch (IOException e)
        {
            Caspian.error("Could not save file for %s", module.getName());
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    public void load()
    {
        Path filepath = getFilepath();
        if (Files.exists(filepath))
        {
            try
            {
                InputStream inputStream = Files.newInputStream(filepath);
                JsonObject json =
                        PARSER.parse(new InputStreamReader(inputStream)).getAsJsonObject();
                module.fromJson(json);
            }

            // error writing file
            catch (IOException e)
            {
                Caspian.error("Could not read file for %s", module.getName());
                e.printStackTrace();
            }
        }
    }
}
