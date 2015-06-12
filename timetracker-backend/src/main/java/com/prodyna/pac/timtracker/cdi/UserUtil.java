package com.prodyna.pac.timtracker.cdi;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRepository;
import com.prodyna.pac.timtracker.model.UserRole;

/**
 * Provides similar service as {@link CurrentUserProducer} for websockets -
 * without relying on any jax-rs injection.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public class UserUtil {
    @Inject
    private UserRepository userRepo;

    @Inject
    private Logger log;

    /**
     * Returns role for given user. To be used from within websocket endpoint -
     * no access to {@link CurrentUser}. Will create new user if user is not
     * registered yet.
     * 
     * @param loggedInUserName
     *            user as given by session
     * @param isAdmin
     *            as given by session
     * @return role of user
     */
    public User getCreateUser(String loggedInUserName, boolean isAdmin) {
        // try to get user by name
        String userName = loggedInUserName;
        User user = userRepo.getByName(userName);
        // if user is null create new user
        if (user == null) {
            UserRole role = UserRole.USER;
            // if user is admin create an admin
            if (isAdmin) {
                role = UserRole.ADMIN;
            }
            user = userRepo.store(new User(userName, role));
            log.info("New user persited: " + user);
        } else {
            log.debug("Detected login of known user: " + user);
        }
        return user;
    }
}
