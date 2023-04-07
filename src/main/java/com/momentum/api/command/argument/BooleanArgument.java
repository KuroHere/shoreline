package com.momentum.api.command.argument;

import com.momentum.api.command.argument.Argument;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BooleanArgument extends Argument<Boolean>
{
    /**
     * Default constructor for argument
     *
     * @param usage
     */
    public BooleanArgument(String usage)
    {
        super(usage);
    }

    /**
     * Returns the argument value
     *
     * @return The arg value
     */
    @Override
    public Boolean getArgValue()
    {
        // TODO: Make cleaner
        return arg.equalsIgnoreCase("t") || arg.equalsIgnoreCase("true") ?
                Boolean.TRUE : arg.equalsIgnoreCase("f") || arg.equalsIgnoreCase(
                        "false") ? Boolean.FALSE : null;
    }

    /**
     * Returns a suggestion of either <tt>true</tt> or <tt>false</tt> based
     * on the typed {@link Argument} literal
     *
     * @return The arg suggestion
     */
    @Override
    public String getSuggestion()
    {
        // String of boolean vals
        String trueStr = Boolean.TRUE.toString();
        String falseStr = Boolean.FALSE.toString();

        // suggestion
        return trueStr.startsWith(arg) ? trueStr : falseStr.startsWith(arg) ?
                falseStr : "";
    }
}
