package com.caspian.api.command.arg;

import com.caspian.api.command.Command;
import com.caspian.api.command.CommandHandler;
import com.caspian.impl.event.chat.ChatInputEvent;

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
 * @see CommandHandler
 */
public abstract class Argument<T>
{
    // The literal string input of the user which will be updated every key
    // press. If the input is null, the argument is left blank.
    private String literal;

    // The value of the argument. This value is only calculated when the
    // command is run.
    private T value;

    /**
     * Automatically completes the argument from the {@link #getSuggestion()}
     * values.
     *
     * @see #getSuggestion()
     */
    public void autoComplete()
    {
        if (literal != null && !literal.isBlank())
        {
            String suggestion = getSuggestion();
            if (suggestion != null)
            {
                setLiteral(suggestion);
            }
        }
    }

    public String getLiteral()
    {
        return literal;
    }

    public T getValue()
    {
        return value;
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
        return null;
    }

    /**
     *
     *
     * @param literal
     *
     * @see Command#setArgInputs(String[])
     * @see CommandHandler#onChatInput(ChatInputEvent)
     */
    public void setLiteral(String literal)
    {
        this.literal = literal;
    }

    /**
     *
     *
     * @param val
     */
    protected void setValue(T val)
    {
        value = val;
    }

    /**
     *
     *
     * @see Command#runCommand()
     */
    public abstract void buildArgument();

    /**
     *
     *
     * @return
     *
     * @see #getSuggestion()
     */
    public abstract String[] getSuggestions();
}
