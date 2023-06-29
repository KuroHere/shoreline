package com.caspian.client.api.waypoint;

import com.caspian.client.Caspian;
import com.caspian.client.api.file.ConfigFile;
import com.caspian.client.init.Managers;
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
 * @see Waypoint
 */
public class WaypointPreset extends ConfigFile
{
    //
    private final String ip;

    /**
     *
     *
     * @param dir
     * @param ip
     *
     *
     */
    public WaypointPreset(Path dir, String ip)
    {
        super(dir, ip);
        this.ip = ip;
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
            for (Waypoint waypoint : Managers.WAYPOINT.getWaypoints())
            {
                if (waypoint.getIp().equalsIgnoreCase(ip))
                {
                    json.add(waypoint.getName(), waypoint.toJson());
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
            Caspian.error("Could not save file for %s.json", ip);
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
                for (Waypoint waypoint : Managers.WAYPOINT.getWaypoints())
                {
                    if (waypoint.getIp().equalsIgnoreCase(ip))
                    {
                        waypoint.fromJson(json);
                    }
                }
            }
            // error writing file
            catch (IOException e)
            {
                Caspian.error("Could not read file for %s.json", ip);
                e.printStackTrace();
            }
        }
    }
}
