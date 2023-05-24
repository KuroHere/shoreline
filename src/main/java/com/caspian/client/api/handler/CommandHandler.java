package com.caspian.client.api.handler;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.manager.CommandManager;
import com.caspian.client.impl.event.chat.ChatInputEvent;
import com.caspian.client.impl.event.chat.ChatMessageEvent;
import com.caspian.client.impl.event.chat.ChatRenderEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.Globals;

import java.util.Arrays;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Command
 * @see CommandManager
 */
public class CommandHandler implements Globals
{
    // Command prefix, used to identify a command in the chat
    public static final String CMD_PREFIX = ".";

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onChatInput(ChatInputEvent event)
    {
        String text = event.getChatText();
        if (text.startsWith(CMD_PREFIX))
        {
            String literal = text.substring(1);
            String[] args = literal.split(" ");
            for (Command command : Managers.COMMAND.getCommands())
            {
                if (command.getName().equalsIgnoreCase(args[0]))
                {
                    command.setArgInputs(Arrays.copyOfRange(args, 1,
                            args.length));
                    break;
                }
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onClientChatMessage(ChatMessageEvent.Client event)
    {
        String msg = event.getMessage();
        if (msg.startsWith(CMD_PREFIX))
        {
            String literal = msg.substring(1);
            String[] args = literal.split(" ");
            for (Command command : Managers.COMMAND.getCommands())
            {
                if (command.getName().equalsIgnoreCase(args[0]))
                {
                    command.runCommand();
                    break;
                }
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onChatRender(ChatRenderEvent event)
    {
        mc.textRenderer.drawWithShadow(event.getMatrices(),
                "", event.getX(), event.getY(), 0xff808080);
    }
}
