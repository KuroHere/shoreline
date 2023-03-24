package com.momentum.api.event;

/**
 * Cancelable event structure. Allows event canceling to prevent
 * event from running
 *
 * @author linus
 * @since 03/20/2023
 */
public interface ICancelable {

    /**
     * Sets the cancelled state
     *
     * @param in The cancelled state
     */
    void setCancelled(boolean in);

    /**
     * Gets the cancelled state
     *
     * @return The cancelled state
     */
    boolean isCanceled();

    /**
     * Gets the cancelable state
     *
     * @return The cancelable state
     */
    boolean isCancelable();
}