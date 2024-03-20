package net.shoreline.client.api.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.shoreline.client.Shoreline;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientInfoFile extends ConfigFile {

    /**
     * @param dir
     */
    public ClientInfoFile(Path dir) {
        super(dir, "ClientInfo");
    }

    @Override
    public void save() {
        try {
            Path filepath = getFilepath();
            if (!Files.exists(filepath)) {
                Files.createFile(filepath);
            }
            JsonObject json = new JsonObject();
            json.addProperty("config", Shoreline.CONFIG.getConfigPreset());
            write(filepath, serialize(json));
        }
        // error writing file
        catch (IOException e) {
            Shoreline.error("Could not save client info file!");
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try {
            Path filepath = getFilepath();
            if (Files.exists(filepath)) {
                String content = read(filepath);
                JsonObject json = parseObject(content);
                if (json.has("config")) {
                    JsonElement element = json.get("config");
                    Shoreline.CONFIG.setConfigPreset(element.getAsString());
                }
            }
        }
        // error writing file
        catch (IOException e) {
            Shoreline.error("Could not read client info file!");
            e.printStackTrace();
        }
    }
}
