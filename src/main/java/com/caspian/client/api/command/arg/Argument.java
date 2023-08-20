package com.caspian.client.api.command.arg;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.config.Config;
import com.caspian.client.api.manager.CommandManager;
import com.caspian.client.impl.event.chat.ChatInputEvent;
import com.caspian.client.util.string.StringUtil;
import com.google.gson.JsonObject;

import java.util.Collection;

/**
 * {@link Command} Argument structure which builds the value from the literal
 * argument.
 *
 * @author linus
 * @since 1.0
 *
 * @param <T> The argument value type
 *
 * @see Command
 * @see CommandManager
 */
public abstract class Argument<T> extends Config<T>
{
    // The literal string input of the user which will be updated every key
    // press. If the input is null, the argument is left blank.
    private String literal;

    /**
     * Initializes the config with a default value. This constructor should
     * not be used to initialize a configuration, instead use the explicit
     * definitions of the configs in {@link com.caspian.client.api.config.setting}.
     *
     * @param name  The unique config identifier
     * @param desc  The config description
     * @throws NullPointerException if value is <tt>null</tt>
     */
    public Argument(String name, String desc)
    {
        // LMAOOOOOOOOO WTF IS THIS
        super(name, desc, (T) new Object());
        this.literal = StringUtil.EMPTY_STRING;
    }

    /**
     * Automatically completes the argument from the {@link #getSuggestion()}
     * values.
     *
     * @see #getSuggestion()
     */
    public String completeLiteral()
    {
        if (literal != null && !literal.isBlank())
        {
            final String suggestion = getSuggestion();
            if (suggestion != null)
            {
                setLiteral(suggestion);
                return suggestion;
            }
        }
        return "";
    }

    /**
     *
     *
     * @throws ArgumentParseException
     *
     * @see Command#onCommandInput()
     */
    public abstract T parse();

    /**
     * Reads all data from a {@link JsonObject} and updates the values of the
     * data in the object.
     *
     * @param jsonObj The data as a json object
     * @see #toJson()
     */
    @Override
    public void fromJson(JsonObject jsonObj)
    {

    }

    /**
     *
     *
     * @return
     */
    public String getLiteral()
    {
        return literal;
    }

    /**
     *
     *
     * @param literal
     *
     * @see CommandManager#onChatInput(ChatInputEvent)
     */
    public void setLiteral(String literal)
    {
        this.literal = literal;
    }

    /**
     *
     *
     * @return
     *
     * @see #getSuggestions()
     */
    public String getSuggestion()
    {
        for (String suggestion : getSuggestions())
        {
            if (suggestion.startsWith(literal))
            {
                return suggestion;
            }
        }
        return literal;
    }

    /**
     *
     *
     * @return
     *
     * @see #getSuggestion()
     */
    public abstract Collection<String> getSuggestions();
}
