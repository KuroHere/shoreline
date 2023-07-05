package com.caspian.client.api.social;

import com.caspian.client.Caspian;
import com.caspian.client.api.file.ConfigFile;
import com.caspian.client.init.Managers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SocialPreset extends ConfigFile
{
    //
    private final SocialRelation relation;

    /**
     *
     *
     * @param dir
     * @param relation
     */
    public SocialPreset(Path dir, SocialRelation relation)
    {
        super(dir, relation.name());
        this.relation = relation;
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
            JsonArray array = new JsonArray();
            for (UUID socials : Managers.SOCIAL.getRelations(relation))
            {
                array.add(socials.toString());
            }
            JsonObject object = new JsonObject();
            object.add(relation.name(), array);
            String jsonString = GSON.toJson(PARSER.parse(object.toString()));
            OutputStream out = Files.newOutputStream(filepath);
            out.write(jsonString.getBytes(StandardCharsets.UTF_8), 0,
                    jsonString.length());
        }
        // error writing file
        catch (IOException e)
        {
            Caspian.error("Could not save file for %s.json",
                    relation.name().toLowerCase());
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
                JsonArray json =
                        PARSER.parse(new InputStreamReader(inputStream)).getAsJsonArray();
                for (JsonElement element : json.asList())
                {
                    Managers.SOCIAL.addRelation(UUID.fromString(element.getAsString()),
                            relation);
                }
            }
            // error writing file
            catch (IOException e)
            {
                Caspian.error("Could not read file for %s.json",
                        relation.name().toLowerCase());
                e.printStackTrace();
            }
        }
    }
}
