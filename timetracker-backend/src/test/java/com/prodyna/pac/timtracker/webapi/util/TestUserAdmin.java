package com.prodyna.pac.timtracker.webapi.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.prodyna.pac.timtracker.cdi.CurrentUser;
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRepository;
import com.prodyna.pac.timtracker.model.UserRole;

/**
 * Produces/ stores an admin user to be used in tests.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RequestScoped
public class TestUserAdmin {

    @Inject
    private UserRepository userRepository;

    public static final String USER_NAME_STRING = "adminUser";

    @Produces
    @CurrentUser
    public User get() {
        User user = userRepository.getByName(USER_NAME_STRING);
        if (user == null) {
            user = userRepository.store(new User(USER_NAME_STRING, UserRole.ADMIN));
        }
        return user;
    }
}
