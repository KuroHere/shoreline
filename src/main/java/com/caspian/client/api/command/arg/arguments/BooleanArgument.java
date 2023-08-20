package com.caspian.client.api.command.arg.arguments;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.util.chat.ChatUtil;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BooleanArgument extends Argument<Boolean>
{
    /**
     * Initializes the config with a default value. This constructor should
     * not be used to initialize a configuration, instead use the explicit
     * definitions of the configs in {@link com.caspian.client.api.config.setting}.
     *
     * @param name  The unique config identifier
     * @param desc  The config description
     * @throws NullPointerException if value is <tt>null</tt>
     */
    public BooleanArgument(String name, String desc)
    {
        super(name, desc);
    }

    /**
     *
     *
     * @see Command#onCommandInput()
     */
    @Override
    public Boolean parse()
    {
        // TODO: Make cleaner
        String literal = getLiteral();
        if (literal.equalsIgnoreCase("t")
                || literal.equalsIgnoreCase("true"))
        {
            return Boolean.TRUE;
        }
        else if (literal.equalsIgnoreCase("f")
                || literal.equalsIgnoreCase("false"))
        {
            return Boolean.FALSE;
        }
        ChatUtil.error("Could not parse Boolean argument!");
        return null;
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Collection<String> getSuggestions()
    {
        return Arrays.asList("true", "false");
    }
}
