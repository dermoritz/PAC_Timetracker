package com.prodyna.pac.timtracker.model;

import static org.junit.Assert.assertTrue;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.prodyna.pac.timtracker.model.util.ArquillianContainer;
import com.prodyna.pac.timtracker.persistence.BaseEntity;
import com.prodyna.pac.timtracker.persistence.Created;
import com.prodyna.pac.timtracker.persistence.EventRepositoryDecorator;
import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.persistence.PersistenceRepository;
import com.prodyna.pac.timtracker.persistence.Removed;
import com.prodyna.pac.timtracker.persistence.Repository;
import com.prodyna.pac.timtracker.persistence.Timestampable;
import com.prodyna.pac.timtracker.persistence.UserCdiDelegatorRepository;

@Transactional(TransactionMode.COMMIT)
@RunWith(Arquillian.class)
public class UserTest {
    
    @Deployment
    public static WebArchive createDeployment() {
        return ArquillianContainer.addClasses(User.class,
                                              UserRole.class,
                                              Repository.class,
                                              PersistenceRepository.class,
                                              Identifiable.class,
                                              BaseEntity.class,
                                              Timestampable.class,
                                              UserRepository.class,
                                              Strings.class,
                                              Preconditions.class,
                                              UserCdiDelegatorRepository.class,
                                              EventRepositoryDecorator.class,
                                              Created.class,
                                              Removed.class);
    }
    
    /**
     * JPA interactions will be conducted on this abstract {@link Repository}.
     */
    @Inject
    private Repository<User> repository;
    
    // these fields are static because Events observed by this TestClass
    // are not observed on the same TestClass instance as @Test is running.
    private static boolean createdFired = false;
    private static boolean removedFired = false;

    public static void createdEventFired(@Observes @Created User conference) {
        createdFired = true;
    }

    public static void removedEventFired(@Observes @Removed User conference) {
        removedFired = true;
    }
    
    @Test
    public void createUser() {
        User user = new User("peter", UserRole.USER);
        repository.store(user);
        assertTrue(createdFired);
    }
    
    
}
