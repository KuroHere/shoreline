package com.caspian.client.api.command.arg;

import com.caspian.client.util.chat.ChatUtil;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Argument
 * @see RuntimeException
 */
public class ArgumentParseException extends RuntimeException
{
    /**
     *
     * @param message
     */
    public ArgumentParseException(String message)
    {
        super(message);
        ChatUtil.error(message);
    }
}
