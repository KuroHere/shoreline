package com.caspian.api.event.listener;

import com.caspian.api.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener
{
    /**
     *
     *
     * @return
     */
    EventPriority priority() default EventPriority.NORMAL;
}
