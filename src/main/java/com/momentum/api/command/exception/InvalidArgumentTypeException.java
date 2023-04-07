package com.momentum.api.command.exception;

import com.momentum.api.command.Command;
import com.momentum.api.command.argument.Argument;

/**
 * Runtime exception thrown when an {@link Argument} type does not match the
 * expected argument type in a {@link Command}
 *
 * @author linus
 * @since 1.0
 *
 * @see Command
 * @see Argument
 */
public class InvalidArgumentTypeException extends RuntimeException
{
    /**
     * Default constructor
     */
    public InvalidArgumentTypeException()
    {
        super();
    }

    /**
     * Provides an error message when thrown
     *
     * @param msg The error message
     */
    public InvalidArgumentTypeException(String msg)
    {
        super(msg);
    }
}
