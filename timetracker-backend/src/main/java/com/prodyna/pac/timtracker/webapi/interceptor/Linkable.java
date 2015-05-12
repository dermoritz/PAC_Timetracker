package com.prodyna.pac.timtracker.webapi.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for {@link LinkedInterceptor}.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Linkable {
    //only marker annotation
}
