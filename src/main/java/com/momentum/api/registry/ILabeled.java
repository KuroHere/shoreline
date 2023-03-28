package com.momentum.api.registry;

/**
 * Data with label in {@link IRegistry} registry, used to get data from
 * the {@link java.util.Map} (i.e. acts as a key for the data)
 *
 * @author linus
 * @since 02/09/2023
 *
 * @see IRegistry
 * @see Registry
 */
public interface ILabeled
{
    /**
     * Gets the unique label
     *
     * @return The label
     */
    String getLabel();
}
