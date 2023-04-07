package com.momentum.api.command.argument;

import com.momentum.api.command.argument.Argument;

import java.util.Collection;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class StringArgument extends Argument<String>
{
    // suggested argument values
    private final String[] suggestions;

    /**
     * Default constructor for argument
     *
     * @param usage The argument usage
     * @param arg   The argument value
     */
    public StringArgument(String usage, String... suggestions)
    {
        super(usage);
        this.suggestions = suggestions;
    }

    /**
     * Default constructor for argument
     *
     * @param usage The argument usage
     * @param arg   The argument value
     */
    public StringArgument(String usage, Collection<String> suggestions)
    {
        this(usage, suggestions.toArray(new String[0]));
    }

    /**
     * Returns the argument value
     *
     * @return The arg value
     */
    @Override
    public String getArgValue()
    {
        return arg;
    }

    /**
     * Returns a suggestion for the argument value. If not suggested value
     * exists, then <tt>null</tt> is returned.
     *
     * @return A suggestion for the arg value
     */
    @Override
    public String getSuggestion()
    {
        String suggestion = null;
        for (String suggest : suggestions)
        {
            // arg curr val matches suggestion
            if (suggest.startsWith(arg))
            {
                suggestion = suggest;
                break;
            }
        }

        // found suggestion
        return suggestion;
    }
}
