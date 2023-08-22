package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.arguments.StringArgument;
import com.caspian.client.init.Managers;
import com.caspian.client.util.chat.ChatUtil;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class VClipCommand extends Command
{
    //
    Argument<String> distanceArg = new StringArgument("Distance", "The " +
            "distance to vertically clip");

    /**
     *
     */
    public VClipCommand()
    {
        super("VClip", "Vertically clips the player");
    }

    /**
     * Runs when the command is inputted in chat
     */
    @Override
    public void onCommandInput()
    {
        final String distance = distanceArg.parse();
        try
        {
            double dist = Double.parseDouble(distance);
            Managers.POSITION.setPositionY(dist);
        }
        catch (NumberFormatException ignored)
        {
            // e.printStackTrace();
            ChatUtil.error("Invalid distance!");
        }
    }
}
