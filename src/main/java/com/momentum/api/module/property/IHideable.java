package com.momentum.api.module.property;

/**
 * Hideable object which has a hidden state that determines whether the
 * object is hidden or visible in the {@link }
 *
 * @author linus
 * @since 03/24/2023
 */
public interface IHideable {

    /**
     * Returns whether the object is hidden
     *
     * @return The hidden state
     */
    boolean isHidden();

    /**
     * Sets the object's hidden state
     *
     * @param hide The new hide state
     */
    void setHidden(boolean hide);
}
