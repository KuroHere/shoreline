package com.momentum.api.config;

import com.momentum.api.config.factory.ConfigFactory;
import com.momentum.api.registry.ILabeled;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration annotation which is used to indicate that a field will be
 * considered a {@link Config} and marks it with a label which will overwrite
 * the {@link ILabeled#getLabel()} value
 *
 * @author linus
 * @since 03/23/2023
 *
 * @see Config
 * @see ConfigFactory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Configuration
{
    /**
     * Gets the config {@link com.momentum.api.registry.ILabeled} label which
     * will be used as the config id
     *
     * @return The config label
     */
    String value();
}
