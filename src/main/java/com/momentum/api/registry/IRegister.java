package com.momentum.api.registry;

/**
 * Register with {@link ILabeled} data.
 *
 * @author linus
 * @since 03/21/2023
 *
 * @param <T> The data type
 *
 * @see Registry
 */
public interface IRegister<T extends ILabeled>
{
    /**
     * Registers the given data to a register, which can later be retrieved
     * using {@link #retrieve(String)}
     *
     * @param l The data label
     * @param data The data
     * @return The registered data
     * @throws NullPointerException if data is <tt>null</tt>
     */
    T register(String l, T data);

    /**
     * Registers the given data to a register, which can later be retrieved
     * using {@link #retrieve(String)}
     *
     * @param data The data
     * @return The registered data
     * @throws NullPointerException if data is <tt>null</tt>
     */
    T register(T data);

    /**
     * Unregisters the given data to a register, which removes its mapping from
     * the register. Unregistering data also frees its {@link ILabeled} label
     *
     * @param data The data
     * @throws NullPointerException if data is <tt>null</tt>
     */
    T unregister(T data);

    /**
     * Retrieves the data from the register by the String value of the label
     *
     * @param l The label of the data
     * @return The data from the register
     * @throws NullPointerException if label is <tt>null</tt>
     */
    T retrieve(String l);

    /**
     * Retrieves the data from the register by the String value of the label,
     * Returns <tt>null</tt> if the retrieved data does not match the class type.
     *
     * @param l The label of the data
     * @param clazz The class type of the data
     * @return The data from the register
     * @throws NullPointerException if label or the class type are <tt>null</tt>
     */
    T retrieve(String l, Class<?> clazz);
}
