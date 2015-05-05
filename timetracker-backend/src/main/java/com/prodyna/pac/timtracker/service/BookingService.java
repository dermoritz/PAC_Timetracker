package com.prodyna.pac.timtracker.service;

import java.util.List;

import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.model.UsersProjects;

/**
 * Specifies all crud operations for {@link Booking}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public interface BookingService {
    /**
     * Persists given booking.
     * 
     * @param booking
     *            booking to be persisted
     * @return booking with id assigned
     */
    Booking create(Booking booking);

    /**
     * Retrieves all bookings for given project.
     * 
     * @param project
     *            project filter
     * @return all bookings for given project
     */
    List<Booking> findByProject(Project project);

    /**
     * Retrieves all bookings for given user.
     * 
     * @param user
     *            user filter
     * @return all bookings for given user
     */
    List<Booking> findByUser(UserService user);

    /**
     * Retrieves all bookings for given user and project.
     * 
     * @param up
     *            user, project combination
     * @return all bookings for given user and project
     */
    List<Booking> findByUserAndProject(UsersProjects up);

    /**
     * Updates given booking.
     * 
     * @param booking
     *            booking to be updated.
     */
    void update(Booking booking);

    /**
     * 
     * @param booking
     *            booking to be deleted
     */
    void delete(Booking booking);
}
