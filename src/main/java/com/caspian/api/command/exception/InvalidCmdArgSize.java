package com.caspian.api.command.exception;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class InvalidCmdArgSize extends RuntimeException
{
    public InvalidCmdArgSize()
    {
        super();
    }

    public InvalidCmdArgSize(String message)
    {
        super(message);
    }
}
