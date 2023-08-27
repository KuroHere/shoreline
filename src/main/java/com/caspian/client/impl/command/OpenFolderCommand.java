package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.util.chat.ChatUtil;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class OpenFolderCommand extends Command
{
    /**
     *
     */
    public OpenFolderCommand()
    {
        super("OpenFolder", "Opens the client configurations folder");
    }

    /**
     * Runs when the command is inputted in chat
     */
    @Override
    public void onCommandInput()
    {
        try
        {
            Desktop.getDesktop().open(Paths.get("Caspian").toFile());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            ChatUtil.error("Failed to open client folder!");
        }
    }
}
