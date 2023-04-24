package com.caspian.api.command.arg;

import com.caspian.api.command.Command;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BooleanArgument extends Argument<Boolean>
{
    /**
     *
     *
     * @see Command#runCommand()
     */
    @Override
    public void buildArgument()
    {
        // TODO: Make cleaner
        String literal = getLiteral();
        Boolean parse = literal.equalsIgnoreCase("t") ||
                literal.equalsIgnoreCase("true") ? Boolean.TRUE :
                literal.equalsIgnoreCase("f") ||
                        literal.equalsIgnoreCase("false") ? Boolean.FALSE
                        : null;
        setValue(parse);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String[] getSuggestions()
    {
        return new String[]
                {
                        Boolean.TRUE.toString(), Boolean.FALSE.toString()
                };
    }
}
