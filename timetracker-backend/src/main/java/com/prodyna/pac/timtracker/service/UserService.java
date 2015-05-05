package com.prodyna.pac.timtracker.service;

import java.util.List;

import javax.ejb.Local;

import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.model.User;

/**
 * Specifies crud methods for {@link User}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Local
public interface UserService {
    /**
     * Persists the given new user.
     * 
     * @param user
     *            user to be persisted
     * @return persisted user, now contains the id
     */
    User create(User user);
    
    /**
     * 
     * @return list of all users
     */
    List<User> findAll();
    
    /**
     * 
     * @param user user to be deleted
     */
    void delete(User user);
    
    /**
     * 
     * @param user updates given user.
     */
    void update(User user);
    
    
}
