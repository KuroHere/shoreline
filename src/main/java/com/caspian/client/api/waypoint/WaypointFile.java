package com.caspian.client.api.waypoint;

import com.caspian.client.Caspian;
import com.caspian.client.api.file.ConfigFile;
import com.caspian.client.init.Managers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Waypoint
 */
public class WaypointFile extends ConfigFile
{
    //
    private final String serverIp;

    /**
     *
     *
     * @param dir
     * @param serverIp
     */
    public WaypointFile(Path dir, String serverIp)
    {
        super(dir, serverIp);
        this.serverIp = serverIp;
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
            for (Waypoint point : Managers.WAYPOINT.getWaypoints())
            {
                if (point.getIp().equalsIgnoreCase(serverIp))
                {
                    final JsonObject obj = new JsonObject();
                    obj.addProperty("tag", point.getName());
                    obj.addProperty("x", point.getX());
                    obj.addProperty("y", point.getY());
                    obj.addProperty("z", point.getZ());
                    array.add(obj);
                }
            }
            write(filepath, serialize(array));
        }
        // error writing file
        catch (IOException e)
        {
            Caspian.error("Could not save file for {}.json!", serverIp);
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
                JsonArray array = parseArray(content);
                if (array == null)
                {
                    return;
                }
                for (JsonElement e : array.asList())
                {
                    JsonObject obj = e.getAsJsonObject();
                    JsonElement tag = obj.get("tag");
                    JsonElement x = obj.get("x");
                    JsonElement y = obj.get("y");
                    JsonElement z = obj.get("z");
                    Managers.WAYPOINT.register(new Waypoint(tag.getAsString(),
                            serverIp, x.getAsDouble(), y.getAsDouble(),
                            z.getAsDouble()));
                }
            }
        }
        // error reading file
        catch (IOException e)
        {
            Caspian.error("Could not read file for {}.json!", serverIp);
            e.printStackTrace();
        }
    }
}
