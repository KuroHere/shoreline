package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.OptionalArg;
import com.caspian.client.api.command.arg.arguments.BooleanArgument;
import com.caspian.client.api.command.arg.arguments.PlayerArgument;
import com.caspian.client.api.command.arg.arguments.StringArgument;
import com.caspian.client.init.Managers;
import com.caspian.client.util.chat.ChatUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

import java.util.Arrays;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class FriendCommand extends Command
{
    //
    @OptionalArg
    Argument<String> actionArgument = new StringArgument("Add/Remove",
            "Whether to add or remove the friend", Arrays.asList("Add", "remove", "del"));
    Argument<PlayerEntity> playerArgument = new PlayerArgument("Player", "The" +
            " player to add/remove friend");
    // Optionally notifies the player you are friending in chat
    @OptionalArg
    Argument<Boolean> notifyArgument = new BooleanArgument("Notify",
            "Notifies the friended player in the chat");

    /**
     *
     */
    public FriendCommand()
    {
        super("Friend", "Adds/Removes a friend from the player list");
    }

    /**
     *
     */
    @Override
    public void onCommandInput()
    {
        final PlayerEntity player = playerArgument.parse();
        if (player != null)
        {
            final String action = actionArgument.parse();
            final Boolean notify = notifyArgument.parse();
            if (action != null)
            {
                if (action.equalsIgnoreCase("add"))
                {
                    ChatUtil.clientSendMessage("Added friend with name " +
                            Formatting.AQUA + player.getEntityName() + Formatting.RESET + "!");
                    Managers.SOCIAL.addFriend(player.getUuid());
                    if (notify != null && notify)
                    {
                        ChatUtil.serverSendMessage(player, "You were friended by %s!",
                                mc.player.getEntityName());
                    }
                }
                else if (action.equalsIgnoreCase("remove")
                        || action.equalsIgnoreCase("del"))
                {
                    ChatUtil.clientSendMessage("Removed friend with name " +
                            Formatting.DARK_BLUE + player.getEntityName() + Formatting.RESET + "!");
                    Managers.SOCIAL.remove(player.getUuid());
                }
            }
            else
            {
                ChatUtil.clientSendMessage("Added friend with name " +
                        Formatting.DARK_BLUE + player.getEntityName() + Formatting.RESET + "!");
                Managers.SOCIAL.addFriend(player.getUuid());
                if (notify != null && notify)
                {
                    ChatUtil.serverSendMessage(player, "You were friended by %s!",
                            mc.player.getEntityName());
                }
            }
        }
    }
}
