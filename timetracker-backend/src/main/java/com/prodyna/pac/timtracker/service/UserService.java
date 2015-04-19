package com.prodyna.pac.timtracker.service;

import java.util.List;

import javax.ejb.Local;

import com.prodyna.pac.timtracker.model.User;


/**
 * Specifies interactions regarding {@link User}.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Local
public interface UserService {
    /**
     * 
     * @param user
     * @return
     */
    User create(User user);
    List<User> findAll();
    void delete(User user);
    void update(User user);
}
