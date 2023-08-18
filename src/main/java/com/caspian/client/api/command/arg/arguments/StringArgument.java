package com.caspian.client.api.command.arg.arguments;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.ArgumentParseException;
import com.caspian.client.util.chat.ChatUtil;

import java.util.*;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class StringArgument extends Argument<String>
{
    //
    private final List<String> allowedValues;

    /**
     *
     *
     * @param name
     * @param desc
     * @param allowedValues
     */
    public StringArgument(String name, String desc, List<String> allowedValues)
    {
        super(name, desc);
        this.allowedValues = allowedValues;
    }

    /**
     *
     *
     * @see Command#onCommandInput()
     */
    @Override
    public String parse() throws ArgumentParseException
    {
        final String literal = getLiteral();
        if (allowedValues.contains(literal))
        {
            return literal;
        }
        throw new ArgumentParseException("Could not parse argument! Allowed values: ");
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Collection<String> getSuggestions()
    {
        return allowedValues;
    }
}
