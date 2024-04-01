package net.shoreline.client.api.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.account.type.MinecraftAccount;
import net.shoreline.client.api.account.type.impl.CrackedAccount;
import net.shoreline.client.api.account.type.impl.MicrosoftAccount;
import net.shoreline.client.api.file.ConfigFile;
import net.shoreline.client.init.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author xgraza
 * @see MinecraftAccount
 * @since 03/31/24
 */
public final class AccountFile extends ConfigFile
{
    /**
     * @param dir
     */
    public AccountFile(Path dir)
    {
        super(dir, "accounts");
    }

    /**
     *
     */
    @Override
    public void save()
    {
        try
        {
            final Path filepath = getFilepath();
            if (!Files.exists(filepath))
            {
                Files.createFile(filepath);
            }

            final JsonArray array = new JsonArray();
            for (final MinecraftAccount account : Managers.ACCOUNT.getAccounts())
            {
                try
                {
                    array.add(account.toJSON());
                }
                catch (RuntimeException e)
                {
                    Shoreline.error(e.getMessage());
                }
            }
            write(filepath, serialize(array));
        }
        // error writing file
        catch (IOException e)
        {
            Shoreline.error("Could not save file for accounts.json!");
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
            final Path filepath = getFilepath();
            if (Files.exists(filepath))
            {
                final String content = read(filepath);
                final JsonArray json = parseArray(content);
                if (json == null)
                {
                    return;
                }

                for (JsonElement element : json.asList())
                {
                    if (!(element instanceof JsonObject object))
                    {
                        continue;
                    }

                    MinecraftAccount account = null;
                    if (object.has("email") && object.has("password")) {
                        account = new MicrosoftAccount(object.get("email").getAsString(),
                                object.get("password").getAsString());
                        if (object.has("username")) {
                            ((MicrosoftAccount) account).setUsername(object.get("username").getAsString());
                        }
                    }
                    else if (object.has("token"))
                    {
                        if (!object.has("username"))
                        {
                            Shoreline.error("Browser account does not have a username set?");
                            continue;
                        }
                        account = new MicrosoftAccount(object.get("token").getAsString());
                        ((MicrosoftAccount) account).setUsername(object.get("username").getAsString());
                    }
                    else
                    {
                        if (object.has("username"))
                        {
                            account = new CrackedAccount(object.get("username").getAsString());
                        }
                    }

                    if (account != null)
                    {
                        Managers.ACCOUNT.register(account);
                    }
                    else
                    {
                        Shoreline.error("Could not parse account JSON.\nRaw: {}", object.toString());
                    }
                }
            }
        }
        // error reading file
        catch (IOException e)
        {
            Shoreline.error("Could not read file for accounts.json!");
            e.printStackTrace();
        }
    }
}
