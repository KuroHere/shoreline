package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.ArgumentParseException;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.Module;
import com.caspian.client.impl.command.*;
import com.caspian.client.impl.event.chat.ChatInputEvent;
import com.caspian.client.impl.event.chat.ChatKeyInputEvent;
import com.caspian.client.impl.event.chat.ChatMessageEvent;
import com.caspian.client.impl.event.chat.ChatRenderEvent;
import com.caspian.client.init.Managers;
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
    private String prefix = ".";

    /**
     * Registers commands to the CommandManager
     */
    public CommandManager()
    {
        Caspian.EVENT_HANDLER.subscribe(this);
        register(
                new HelpCommand(),
                new FriendCommand(),
                new PrefixCommand(),
                new DrawnCommand()
        );
        //
        for (Module module : Managers.MODULE.getModules())
        {
            register(new ModuleCommand(module));
        }
        Caspian.info("Registered {} commands!", commands.size());
    }

    /**
     * Reflects arguments and assigns them to their respective commands
     */
    public void postInit()
    {
        // get args
        for (Command command : commands)
        {
            command.reflectConfigs();
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onChatInput(ChatInputEvent event)
    {
        chat = new StringBuilder();
        final String text = event.getChatText().trim();
        if (text.startsWith(prefix))
        {
            String literal = text.substring(1);
            String[] args = literal.split(" ");
            //
            for (Command command : getCommands())
            {
                String name = command.getName();
                if (name.equals(args[0]))
                {
                    chat.append(args[0]).append(" ");
                    for (int i = 1; i < args.length; i++)
                    {
                        Argument<?> arg = command.getArg(i - 1);
                        arg.setLiteral(args[i]);
                        if (i + 1 < args.length)
                        {
                            chat.append(arg.getLiteral());
                        }
                        else
                        {
                            chat.append(arg.getSuggestion());
                        }
                        chat.append(" ");
                    }
                    break;
                }
                else if (name.startsWith(args[0]))
                {
                    chat.append(command.getName());
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
        String msg = event.getMessage().trim();
        if (msg.startsWith(prefix))
        {
            event.cancel();
            mc.inGameHud.getChatHud().addToMessageHistory(msg);
            //
            String literal = msg.substring(1);
            String[] args = literal.split(" ");
            for (Command command : getCommands())
            {
                String name = command.getName();
                if (name.equals(args[0]))
                {
                    try
                    {
                        command.onCommandInput();
                        chat = new StringBuilder();
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
     * @param event
     */
    @EventListener
    public void onChatKeyInput(ChatKeyInputEvent event)
    {
        if (event.getKeycode() == GLFW.GLFW_KEY_TAB)
        {
            String msg = event.getChatText().trim();
            if (msg.startsWith(prefix))
            {
                event.cancel();
                String literal = msg.substring(1);
                String[] args = literal.split(" ");
                for (Command command : getCommands())
                {
                    String name = command.getName();
                    if (args.length > 1 && name.equals(args[0]))
                    {
                        Argument<?> tail = command.getLastArg();
                        tail.completeLiteral();
                        event.setChatText(command.getLiteral(prefix));
                        break;
                    }
                    else if (name.startsWith(args[0]))
                    {
                        event.setChatText(prefix + name);
                        break;
                    }
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

    /**
     *
     * @param prefix
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
}
