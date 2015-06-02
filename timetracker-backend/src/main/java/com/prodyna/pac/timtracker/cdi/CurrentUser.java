package com.prodyna.pac.timtracker.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.prodyna.pac.timtracker.model.User;

/**
 * To qualify injections of {@link User} to get the entity of user currently
 * logged in.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface CurrentUser {
    //
}
