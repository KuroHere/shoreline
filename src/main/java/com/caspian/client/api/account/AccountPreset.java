package com.caspian.client.api.account;

import com.caspian.client.Caspian;
import com.caspian.client.api.file.ConfigFile;
import com.caspian.client.init.Managers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Account
 */
public class AccountPreset extends ConfigFile
{
    //
    private final AccountType type;

    /**
     *
     *
     * @param dir
     * @param type
     */
    public AccountPreset(Path dir, AccountType type)
    {
        super(dir, type.name().toLowerCase() + ".json");
        this.type = type;
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
            JsonObject json = new JsonObject();
            for (Account account : Managers.ACCOUNT.getAccounts())
            {
                if (account.getType() == type)
                {
                    json.add(account.getName(), account.toJson());
                }
            }
            String jsonString = GSON.toJson(PARSER.parse(json.toString()));
            OutputStream out = Files.newOutputStream(filepath);
            out.write(jsonString.getBytes(StandardCharsets.UTF_8), 0,
                    jsonString.length());
        }
        // error writing file
        catch (IOException e)
        {
            Caspian.error("Could not save file for %s.json", type.name()
                    .toLowerCase());
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
                for (Map.Entry<String, JsonElement> entry : json.entrySet())
                {
                    Managers.ACCOUNT.register(new Account(type, entry.getKey(),
                            entry.getValue().getAsString()));
                }
            }
            // error writing file
            catch (IOException e)
            {
                Caspian.error("Could not read file for %s.json", type.name()
                        .toLowerCase());
                e.printStackTrace();
            }
        }
    }
}
