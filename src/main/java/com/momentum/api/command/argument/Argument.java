package com.momentum.api.command.argument;

import com.momentum.api.command.Command;

/**
 * Argument data holder for a {@link Command} which provides a argument
 * invoker structure for the chat command.
 *
 * @author linus
 * @since 1.0
 *
 * @param <T> The argument value type
 *
 * @see Command
 * @see BooleanArgument
 * @see StringArgument
 */
public abstract class Argument<T>
{
    // argument correct usage
    private final String usage;

    // argument curr value
    protected String arg;

    /**
     * Default constructor for argument
     *
     * @param arg The argument value
     */
    public Argument(String usage)
    {
        this.usage = usage;
        this.arg = null;
    }

    /**
     * Default constructor for argument
     *
     * @param arg The argument value
     */
    public Argument(String usage, String arg)
    {
        this.usage = usage;
        this.arg = arg;
    }

    /**
     * Completes the current {@link Argument} and autofill the value with the first
     * suggested value in the suggestions list
     *
     * @return The completed argument
     */
    public String tabComplete()
    {
        String suggestion = getSuggestion();
        if (suggestion != null)
        {
            // complete arg val
            return arg = suggestion;
        }

        // no suggested val
        return arg;
    }

    /**
     * Returns a correct usage of the argument
     *
     * @return The correct use of arg
     */
    public String getUsage()
    {
        return usage;
    }

    /**
     * Returns the argument literal String value
     *
     * @return The arg literal value
     */
    public String getArg()
    {
        return arg;
    }

    /**
     * Sets the argument value
     *
     * @param in The arg value
     */
    public void setArgValue(String in)
    {
        arg = in;
    }

    /**
     * Returns the argument value
     *
     * @return The arg value
     */
    public abstract T getArgValue();

    /**
     * Returns a suggestion for the argument value. If a suggested value
     * does not exist, then <tt>null</tt> is returned.
     *
     * @return A suggestion for the arg value
     */
    public abstract String getSuggestion();
}
