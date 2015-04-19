package com.prodyna.pac.timtracker.model;

/**
 * User roles within time tracker.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public enum UserRole {
    /**
     * Has all permissions.
     */
    ADMIN,
    /**
     * Can see all bookings. Can link user and projects.
     */
    MANAGER,
    /**
     * Can see/update own data/bookings.
     */
    USER;
}
