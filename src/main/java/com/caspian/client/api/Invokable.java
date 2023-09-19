package com.caspian.client.api;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @param <T> The argument type
 */
public interface Invokable<T>
{
    /**
     *
     *
     * @param arg The argument
     */
    void invoke(T arg);
}
