package com.caspian.api.command.exception;

public class InvalidCmdArgValue extends RuntimeException
{
    public InvalidCmdArgValue()
    {
        super();
    }

    public InvalidCmdArgValue(String message)
    {
        super(message);
    }
}
