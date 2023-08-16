package com.caspian.client.api.social;

import com.caspian.client.Caspian;
import com.caspian.client.api.file.ConfigFile;
import com.caspian.client.init.Managers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SocialFile extends ConfigFile
{
    //
    private final SocialRelation relation;

    /**
     *
     *
     * @param dir
     * @param relation
     */
    public SocialFile(Path dir, SocialRelation relation)
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
        try
        {
            Path filepath = getFilepath();
            if (!Files.exists(filepath))
            {
                Files.createFile(filepath);
            }
            final JsonArray array = new JsonArray();
            for (UUID socials : Managers.SOCIAL.getRelations(relation))
            {
                array.add(new JsonPrimitive(socials.toString()));
            }
            write(filepath, serialize(array));
        }
        // error writing file
        catch (IOException e)
        {
            Caspian.error("Could not save file for {}.json!",
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
        try
        {
            Path filepath = getFilepath();
            if (Files.exists(filepath))
            {
                final String content = read(filepath);
                JsonArray json = parseArray(content);
                if (json == null)
                {
                    return;
                }
                for (JsonElement element : json.asList())
                {
                    Managers.SOCIAL.addRelation(
                            UUID.fromString(element.getAsString()), relation);
                }
            }
        }
        // error reading file
        catch (IOException e)
        {
            Caspian.error("Could not read file for {}.json!",
                    relation.name().toLowerCase());
            e.printStackTrace();
        }
    }
}
