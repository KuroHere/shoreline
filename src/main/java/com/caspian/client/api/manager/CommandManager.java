package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.ArgumentParseException;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.command.FriendCommand;
import com.caspian.client.impl.command.HelpCommand;
import com.caspian.client.impl.event.chat.ChatInputEvent;
import com.caspian.client.impl.event.chat.ChatMessageEvent;
import com.caspian.client.impl.event.chat.ChatRenderEvent;
import com.caspian.client.util.Globals;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Command
 */
public class CommandManager implements Globals
{
    //
    private final List<Command> commands = new ArrayList<>();
    //
    private StringBuilder chat = new StringBuilder();
    // Command prefix, used to identify a command in the chat
    public static final String CMD_PREFIX = ".";

    /**
     *
     */
    public CommandManager()
    {
        Caspian.EVENT_HANDLER.subscribe(this);
    }

    /**
     * Registers commands to the CommandManager
     */
    public void postInit()
    {
        register(
                new HelpCommand(),
                new FriendCommand()
        );
        // get args
        for (Command command : commands)
        {
            command.reflectConfigs();
        }
        Caspian.info("Registered {} commands!", commands.size());
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onChatInput(ChatInputEvent event)
    {
        final String text = event.getChatText();
        if (text.startsWith(CMD_PREFIX))
        {
            String literal = text.substring(1);
            String[] args = literal.split(" ");
            //
            chat = new StringBuilder();
            for (Command command : getCommands())
            {
                if (command.getName().equalsIgnoreCase(args[0]))
                {
                    chat.append(args[0]);
                    for (int i = 1; i < args.length; i++)
                    {
                        Argument<?> arg = command.getArg(i - 1);
                        arg.setLiteral(args[i]);
                        if (i < args.length - 1)
                        {
                            chat.append(arg.getLiteral());
                        }
                        else
                        {
                            if (event.getKeyCode() == GLFW.GLFW_KEY_TAB)
                            {
                                arg.completeLiteral();
                            }
                            chat.append(arg.getSuggestion());
                        }
                    }
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
            for (Command command : getCommands())
            {
                if (command.getName().equalsIgnoreCase(args[0]))
                {
                    try
                    {
                        command.onCommandInput();
                    }
                    catch (ArgumentParseException e)
                    {
                        e.printStackTrace();
                    }
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
                chat.toString(), event.getX(), event.getY(), 0xff808080);
    }

    /**
     *
     *
     * @param commands
     */
    private void register(Command... commands)
    {
        for (Command command : commands)
        {
            register(command);
        }
    }

    /**
     *
     *
     * @param command
     */
    private void register(Command command)
    {
        commands.add(command);
    }

    /**
     *
     *
     * @return
     */
    public List<Command> getCommands()
    {
        return commands;
    }
}
