package com.momentum.api.command;

import com.momentum.api.registry.Registry;
import com.momentum.api.util.Globals;
import com.momentum.impl.event.ChatInputEvent;
import com.momentum.impl.event.ChatMessageEvent;
import com.momentum.impl.event.ChatTextRenderEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Dispatcher/Handler for {@link Command} which runs commands based on input
 * in the {@link net.minecraft.client.gui.screen.ChatScreen}.
 *
 * @author linus
 * @since 1.0
 *
 * @see Command
 */
public class CommandDispatcher extends Registry<Command> implements Globals
{
    // command prefix, used to identify a command in the chat
    public static final String CMD_PREFIX = ".";

    /**
     * Initializes the command register
     */
    public CommandDispatcher()
    {

    }

    /**
     *
     *
     * @param event
     */
    @Deprecated
    public void onChatMessage(ChatMessageEvent event)
    {
        // sent message
        String message = event.getContent();
        if (message.startsWith(CMD_PREFIX))
        {
            // remove command prefix
            String literal = message.substring(1);

            // split into args
            String[] args = literal.split(" ");

            // find executable
            for (Command command : getCommands())
            {
                // found, invoke command exec
                if (command.equals(args[0]))
                {
                    command.invoke();
                    break;
                }
            }
        }
    }

    // suggestion String displayed behind text
    private String chatSuggestion;

    /**
     *
     *
     * @param event
     */
    @Deprecated
    public void onChatKeyPressed(ChatInputEvent event)
    {
        // text in chat
        String text = event.getChatText();
        if (text.startsWith(CMD_PREFIX))
        {
            // remove command prefix
            String literal = text.substring(1);

            // split into args
            String[] args = literal.split(" ");

            // find executable
            for (Command command : getCommands())
            {
                // found, build args
                if (command.equals(args[0]))
                {
                    // build suggestion
                    StringBuilder suggestion = new StringBuilder(text);
                    for (int i = args.length; i <= command.getArgsLength(); i++)
                    {
                        suggestion.append(command.getArg(i).getUsage());
                    }

                    // update suggestion
                    chatSuggestion = suggestion.toString();

                    // tab complete
                    if (event.getKeycode() != GLFW.GLFW_KEY_UNKNOWN
                            && event.getKeycode() == GLFW.GLFW_KEY_TAB)
                    {
                        command.tabComplete();
                    }

                    // update args and suggestions
                    else
                    {
                        command.setArgValues(args);
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
    @Deprecated
    public void onChatTextRender(ChatTextRenderEvent event)
    {
        // render suggestion text
        if (chatSuggestion != null)
        {
            event.setChatText(chatSuggestion);
            event.setCancelled(true);
        }
    }

    /**
     * Returns a list of all commands in the dispatcher
     *
     * @return All commands in dispatcher
     */
    public Collection<Command> getCommands()
    {
        return register.values();
    }

    /**
     * Returns a list of the command usages in the dispatcher
     *
     * @return All command usage in dispatcher
     */
    public Collection<String> getCommandUsages()
    {
        // build usage list
        Collection<String> usages = new ArrayList<>();
        for (Command command : getCommands())
        {
            usages.add(command.getUsage());
        }

        return usages;
    }
}
