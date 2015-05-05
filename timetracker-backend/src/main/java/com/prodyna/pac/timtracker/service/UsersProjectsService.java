package com.prodyna.pac.timtracker.service;

import java.util.List;

import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UsersProjects;

/**
 * Specifies services for user->project registration.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public interface UsersProjectsService {
    /**
     * Registers given user to project.
     * 
     * @param user
     *            a existent (persisted) user
     * @param project
     *            a existent (persisted) project
     */
    void registerUser(User user, Project project);

    /**
     * Unregisters a user from a project. The user will not more be able to book
     * on this project. <b>will only work if there are no booking yet</b> To
     * render a user unable to create new bookings detract permissions
     * accordingly.
     * 
     * @param user
     *            a existent (persisted) user
     * @param project
     *            a existent (persisted) project
     * @return true on success, false if unregistration is impossible due to
     *         bookings already linking
     */
    boolean unregisterUser(User user, Project project);

    /**
     * retrieves all Projects for given user.
     * 
     * @param user user
     * @return List for given user
     */
    List<UsersProjects> findForUser(User user);

}
