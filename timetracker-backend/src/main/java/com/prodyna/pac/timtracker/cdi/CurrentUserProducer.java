package com.prodyna.pac.timtracker.cdi;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRepository;
import com.prodyna.pac.timtracker.model.UserRole;

/**
 * This reads {@link SecurityContext} to get logged in user and tries to find
 * him in data base. If not found a new user will be persisted with Role
 * {@link UserRole#USER}.
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

    @Inject
    private Logger log;

    @Inject
    private transient UserRepository userRepo;

    @Context
    private SecurityContext secContext;

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
        // try to get user by name
        String userName = secContext.getUserPrincipal().getName();
        User user = userRepo.getByName(userName);
        // if user is null create new user
        if (user == null) {
            UserRole role = UserRole.USER;
            // if user is admin create an admin
            if (secContext.isUserInRole(UserRole.ADMIN.name())) {
                role = UserRole.ADMIN;
            }
            user = userRepo.store(new User(userName, role));
            log.info("New user persited: " + user);
        } else {
            log.debug("Detected login of known user: " + user);
        }
        return user;
    }

    @Override
    public User getContext(Class<?> type) {
        if (type.equals(User.class)){
            return getCurrentUser();
        }
        return null;
    }

}
