package com.prodyna.pac.timtracker.model;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Typed;

import com.prodyna.pac.timtracker.persistence.PersistenceRepository;

/**
 * Repository for {@link User}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Stateless
@LocalBean
@Typed(UserRepository.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserRepository extends PersistenceRepository<User> {

    /**
     * Creates {@link PersistenceRepository} with {@link User} as type.
     */
    public UserRepository() {
        super(User.class);
    }

    /**
     * Queries users by name (case insensitive).
     * 
     * @param name
     *            user name
     * @return user with given name
     */
    public User getByName(String name) {
        List<User> results = getByStringAttribute(User_.name, name);
        // since name is a key there should be either one or zero results.
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }
}
