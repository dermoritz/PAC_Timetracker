package com.prodyna.pac.timtracker.model;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Typed;

import com.prodyna.pac.timtracker.persistence.PersistenceRepository;
/**
 * Repository for {@link User}.
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

}
