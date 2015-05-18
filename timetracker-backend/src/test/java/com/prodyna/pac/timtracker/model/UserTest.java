package com.prodyna.pac.timtracker.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.constraints.AssertFalse;

import org.hamcrest.core.Is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.webapp25.NullCharType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.prodyna.pac.timtracker.cdi.EntityManagerProducer;
import com.prodyna.pac.timtracker.model.util.PersistenceArquillianContainer;
import com.prodyna.pac.timtracker.persistence.BaseEntity;
import com.prodyna.pac.timtracker.persistence.BaseEntity_;
import com.prodyna.pac.timtracker.persistence.Created;
import com.prodyna.pac.timtracker.persistence.EventRepositoryDecorator;
import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.persistence.PersistenceRepository;
import com.prodyna.pac.timtracker.persistence.Removed;
import com.prodyna.pac.timtracker.persistence.Repository;
import com.prodyna.pac.timtracker.persistence.Timestampable;
import com.prodyna.pac.timtracker.persistence.UserCdiDelegatorRepository;

/**
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Transactional(TransactionMode.COMMIT)
@RunWith(Arquillian.class)
public class UserTest {

    @Deployment
    public static WebArchive createDeployment() {
        return PersistenceArquillianContainer.addClasses(User.class,
                                                         User_.class,
                                                         BaseEntity_.class,
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
                                                         Removed.class,
                                                         EntityManagerProducer.class);
    }

    /**
     * JPA interactions will be conducted on this abstract {@link Repository}.
     */
    @Inject
    private Repository<User> repository;

    @Inject
    private UserRepository userRepository;

    // these fields are static because Events observed by this TestClass
    // are not observed on the same TestClass instance as @Test is running.
    private static boolean createdFired = false;
    private static boolean removedFired = false;

    /**
     * Observes created events and set the flag.
     * 
     * @param user
     */
    public static void createdEventFired(@Observes @Created User user) {
        createdFired = true;
    }

    /**
     * Observes removed event and sets a flag.
     * 
     * @param user
     */
    public static void removedEventFired(@Observes @Removed User user) {
        removedFired = true;
    }

    /**
     * Resets flags before each test.
     */
    @Before
    public void before() {
        createdFired = false;
        removedFired = false;
    }

    /**
     * Tests creation and peristing a user.
     */
    @Test
    public void createUser() {
        User user = new User("klaus", UserRole.USER);
        User storedUser = repository.store(user);
        assertTrue(createdFired);
        assertNotNull(storedUser.getId());
    }

    /**
     * Tests removal of a user.
     */
    @Test
    public void removeUser() {
        User user = repository.store(new User("peter", UserRole.MANAGER));
        Long id = user.getId();
        assertNotNull(id);
        repository.remove(user);
        assertTrue(removedFired);
        User fetchedUser = repository.get(id);
        assertNull(fetchedUser);
    }

    /**
     * Checks setting role.
     */
    @Test
    public void setRole() {
        User user = repository.store(new User("sübülle", UserRole.MANAGER));
        Long id = user.getId();
        assertNotNull(id);
        User fetchedUser = repository.get(id);
        assertThat(fetchedUser.getRole(), is(UserRole.MANAGER));
        fetchedUser.setRole(UserRole.USER);
        assertThat(fetchedUser.getRole(), is(UserRole.USER));
    }
    
    /**
     * Checks query by name.
     */
    @Test
    public void queryByName() {
        String name = "sübrülle";
        User user = repository.store(new User(name, UserRole.MANAGER));
        Long id = user.getId();
        assertNotNull(id);
        //query should be case insensitive
        User byName = userRepository.getByName(name.toUpperCase());
        assertNotNull(byName);
        assertThat(byName.getName(), is(name));
    }

    /**
     * to check exceptions.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Tests if exception is thrown if 2 users with same name are stored.
     */
    @Test
    public void createNameClash() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Unable to commit the transaction.");
        String name = "OJ";
        User user1 = new User(name, UserRole.USER);
        User user2 = new User(name, UserRole.MANAGER);
        repository.store(user1);
        repository.store(user2);
    }

    /**
     * Checks exception thrown if role is null in constructor.
     */
    @Test
    public void createWithNullRole() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Role");
        new User("blub", null);
    }

    /**
     * Checks exception if null role is set via setter.
     */
    @Test
    public void setNullRole() {
        User user = new User("p", UserRole.USER);
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Role");
        user.setRole(null);
    }

    /**
     * Checks exception on null/empty name.
     */
    @Test
    public void nullOrEmptyName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name");
        new User("", UserRole.MANAGER);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name");
        new User(null, UserRole.ADMIN);
    }
}
