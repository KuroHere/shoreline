package net.shoreline.client.api.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.file.ConfigFile;
import net.shoreline.client.init.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author linus
 * @see Account
 * @since 1.0
 */
public class AccountFile extends ConfigFile {

    /**
     * @param dir
     */
    public AccountFile(Path dir) {
        super(dir, "accounts");
    }

    /**
     *
     */
    @Override
    public void save() {
        try {
            Path filepath = getFilepath();
            if (!Files.exists(filepath)) {
                Files.createFile(filepath);
            }
            JsonArray array = new JsonArray();
            for (Account account : Managers.ACCOUNT.getAccounts()) {
                JsonObject json = new JsonObject();
                json.addProperty("email", account.getName());
                json.addProperty("password", account.getPassword());

                if (account.isPremium() && account.isUsernameSet()) {
                    json.addProperty("username", account.getUsernameOrEmail());
                }

                array.add(json);
            }
            write(filepath, serialize(array));
        }
        // error writing file
        catch (IOException e) {
            Shoreline.error("Could not save file for accounts.json!");
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    public void load() {
        try {
            Path filepath = getFilepath();
            if (Files.exists(filepath)) {
                final String content = read(filepath);
                JsonArray json = parseArray(content);
                if (json == null) {
                    return;
                }
                for (JsonElement e : json.asList()) {
                    final JsonObject obj = e.getAsJsonObject();
                    final String email = obj.get("email").getAsString();
                    final String password = obj.get("password").getAsString();

                    final Account account = new Account(email, password);
                    if (obj.has("username")) {
                        account.setUsername(obj.get("username").getAsString());
                    }

                    Managers.ACCOUNT.register(account);
                }
            }
        }
        // error reading file
        catch (IOException e) {
            Shoreline.error("Could not read file for accounts.json!");
            e.printStackTrace();
        }
    }
}
