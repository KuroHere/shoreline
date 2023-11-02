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
public class HClipCommand extends Command
{
    //
    Argument<String> distanceArgument = new StringArgument("Distance", "The " +
            "distance to horizontally clip");

    /**
     *
     */
    public HClipCommand()
    {
        super("HClip", "Horizontally clips the player");
    }

    /**
     * Runs when the command is inputted in chat
     */
    @Override
    public void onCommandInput()
    {
        final String distance = distanceArgument.getValue();
        try
        {
            double dist = Double.parseDouble(distance);
            double rad = Math.toRadians(mc.player.getYaw() + 90);
            double x = Math.cos(rad) * dist;
            double z = Math.sin(rad) * dist;
            Managers.POSITION.setPositionXZ(x, z);
        }
        catch (NumberFormatException ignored)
        {
            // e.printStackTrace();
            ChatUtil.error("Invalid distance!");
        }
    }
}
