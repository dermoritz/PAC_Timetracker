package com.prodyna.pac.timtracker.cdi;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRole;

/**
 * This reads {@link SecurityContext} to get logged in user and with the help of
 * {@link UserUtil} it will fetch the user from data base or create a new one.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RequestScoped
@Provider
public class CurrentUserProducer implements Serializable, ContextResolver<User> {

    /**
     * Default
     */
    private static final long serialVersionUID = 1L;

    /**
     * If user has this role in {@link SecurityContext#isUserInRole(String)} the
     * user will become an admin.
     */
    public static final String ADMIN_ROLE = UserRole.ADMIN.name();

    @Context
    private SecurityContext secContext;

    @Inject
    private UserUtil userUtil;

    /**
     * Tries to find logged in user in user db (by name) and returns it. If not
     * found a new user with role {@link UserRole#USER} is created.
     * 
     * @return found user a new user with role user
     */
    @Produces
    @CurrentUser
    public User getCurrentUser() {
        if (secContext == null) {
            throw new IllegalStateException("Can't inject security context - security context is null.");
        }
        return userUtil.getCreateUser(secContext.getUserPrincipal().getName(),
                                      secContext.isUserInRole(UserRole.ADMIN.name()));
    }

    @Override
    public User getContext(Class<?> type) {
        if (type.equals(User.class)) {
            return getCurrentUser();
        }
        return null;
    }

}
