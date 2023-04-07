package com.momentum.api.command.argument;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @param <T> The enum type
 */
public class EnumArgument<T extends Enum<T>> extends Argument<T>
{
    // enum values
    private final T[] values;

    /**
     * Default constructor for argument
     *
     * @param usage
     * @param values
     */
    public EnumArgument(String usage, T[] values)
    {
        super(usage);
        this.values = values;
    }

    /**
     * Returns the argument value
     *
     * @return The arg value
     */
    @SuppressWarnings("unchecked")
    @Override
    public T getArgValue()
    {
        // enum value
        return (T) Enum.valueOf(values[0].getClass(), arg);
    }

    /**
     * Returns a suggestion for the argument value. If a suggested value
     * does not exist, then <tt>null</tt> is returned.
     *
     * @return A suggestion for the arg value
     */
    @Override
    public String getSuggestion()
    {
        String suggestion = null;
        for (T val : values)
        {
            // enum value matches arg
            if (val.name().toLowerCase().startsWith(arg))
            {
                suggestion = val.name().toLowerCase();
                break;
            }
        }

        // return suggestion
        return suggestion;
    }
}
