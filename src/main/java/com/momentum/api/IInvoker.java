package com.momentum.api;

/**
 * Invoker structure. Allows class to invoke the underlying method based on given
 * argument(s)
 *
 * @author linus
 * @since 03/23/2023
 *
 * @param <T> The argument type
 */
public interface IInvoker<T>
{
    /**
     * Invokes for the given argument
     *
     * @param arg The argument
     */
    void invoke(T arg);

    /**
     * Invokes for the given arguments
     *
     * @param args The arguments
     */
    void invoke(T[] args);
}
