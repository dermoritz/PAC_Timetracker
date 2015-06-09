package com.prodyna.pac.timtracker.webapi.webSockets;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.prodyna.pac.timtracker.cdi.CurrentUser;
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRepository;
import com.prodyna.pac.timtracker.model.UserRole;

public class UserUtil {
    @Inject
    private UserRepository userRepo;

    @Inject
    private Logger log;

    /**
     * Returns role for given user. To be used from within websocket endpoint -
     * no access to {@link CurrentUser}. Will create new user if user is not registered yet.
     * 
     * @param loggedInUserName user as given by session
     * @param isAdmin as given by session 
     * @return role of user
     */
    public UserRole getCreateUser(String loggedInUserName) {
        // try to get user by name
        String userName = loggedInUserName;
        User user = userRepo.getByName(userName);
        // if user is null create new user
        if (user == null) {
            UserRole role = UserRole.USER;
            user = userRepo.store(new User(userName, role));
            log.info("New user persited: " + user);
        } else {
            log.debug("Detected login of known user: " + user);
        }
        return user.getRole();
    }
}
