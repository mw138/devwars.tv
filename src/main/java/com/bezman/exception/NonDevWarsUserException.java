package com.bezman.exception;

public class NonDevWarsUserException extends IllegalArgumentException
{
    public NonDevWarsUserException()
    {
        super("User must be Native");
    }
}
