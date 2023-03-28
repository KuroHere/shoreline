package com.momentum.api.module.exceptions;

/**
 * Thrown when two conflicting interfaces are added to the same class
 *
 * @author linus
 * @since 03/22/2023
 */
public class IncompatibleInterfaceException extends RuntimeException
{
    /**
     * Default constructor
     */
    public IncompatibleInterfaceException()
    {
        super();
    }

    /**
     * Constructs a new throwable with the specified detail message.
     *
     * @param message The detail message
     */
    public IncompatibleInterfaceException(String message)
    {
        super(message);
    }
}
