package com.momentum.api.feature;

/**
 * Invokable property enabling the ability to invoke {@link com.momentum.api.feature.Feature} feature without arguments
 *
 * @author linus
 * @since 02/02/2023
 */
public interface Invokable {

    /**
     * Invoke the feature
     */
    void invoke();
}
