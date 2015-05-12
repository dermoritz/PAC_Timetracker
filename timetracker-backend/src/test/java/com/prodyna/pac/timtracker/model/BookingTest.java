package com.prodyna.pac.timtracker.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
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
import com.prodyna.pac.timtracker.persistence.BookingCdiDelegatorRepository;
import com.prodyna.pac.timtracker.persistence.Created;
import com.prodyna.pac.timtracker.persistence.EventRepositoryDecorator;
import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.persistence.PersistenceRepository;
import com.prodyna.pac.timtracker.persistence.Removed;
import com.prodyna.pac.timtracker.persistence.Repository;
import com.prodyna.pac.timtracker.persistence.Timestampable;
import com.prodyna.pac.timtracker.persistence.UsersProjectsCdiDelegatorRepository;

/**
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Transactional(TransactionMode.COMMIT)
@RunWith(Arquillian.class)
public class BookingTest {

    @Deployment
    public static WebArchive createDeployment() {
        return PersistenceArquillianContainer.addClasses(Project.class,
                                              User.class,
                                              UsersProjects.class,
                                              Booking.class,
                                              UserRole.class,
                                              BookingRepository.class,
                                              BookingCdiDelegatorRepository.class,
                                              UsersProjectsRepository.class,
                                              UsersProjectsCdiDelegatorRepository.class,
                                              Repository.class,
                                              PersistenceRepository.class,
                                              Identifiable.class,
                                              BaseEntity.class,
                                              Timestampable.class,
                                              Strings.class,
                                              Preconditions.class,
                                              EventRepositoryDecorator.class,
                                              Created.class,
                                              Removed.class,
                                              EntityManagerProducer.class);
    }

    /**
     * JPA interactions will be conducted on this abstract {@link Repository}.
     */
    @Inject
    private Repository<Booking> repository;

    @Inject
    private Repository<UsersProjects> upRepository;

    // these fields are static because Events observed by this TestClass
    // are not observed on the same TestClass instance as @Test is running.
    private static boolean createdFired = false;
    private static boolean removedFired = false;

    /**
     * Observes created events and set the flag.
     * 
     * @param project
     */
    public static void createdEventFired(@Observes @Created Booking booking) {
        createdFired = true;
    }

    /**
     * Observes removed event and sets a flag.
     * 
     * @param user
     */
    public static void removedEventFired(@Observes @Removed Booking booking) {
        removedFired = true;
    }

    @Before
    public void resetFlags() {
        createdFired = false;
        removedFired = false;
    }

    /**
     * Creates and removes a booking.
     */
    @Test
    public void createRemove() {
        // register a user to a project
        UsersProjects storedUp = createUp();
        // typed Booking event should not be fired
        assertFalse(createdFired);
        Booking storedBooking = repository.store(new Booking(storedUp, new Date(0), new Date(2)));
        assertNotNull(storedBooking.getId());
        assertTrue(createdFired);
        repository.remove(storedBooking);
        assertTrue(removedFired);
        assertNull(repository.get(storedBooking.getId()));
    }

    /**
     * Test setters for start and end date.
     */
    @Test
    public void changeStartEnd() {
        // register a user to a project
        UsersProjects storedUp = createUp();
        Booking storedBooking = repository.store(new Booking(storedUp, new Date(0), new Date(2)));
        assertNotNull(storedBooking.getId());
        Date newEnd = new Date(3);
        Date newStart = new Date(1);
        storedBooking.setEnd(newEnd);
        storedBooking.setStart(newStart);
        assertThat(storedBooking.getEnd(), is(newEnd));
    }

    /**
     * Rule to check exceptions.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * It should not be possible to create a booking with a
     * {@link UsersProjects} that is not persisted yet - the user is not
     * registered to project yet.
     */
    @Test
    public void notStoredUserProject() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("id");
        UsersProjects usersProjects = new UsersProjects(new User("klaus", UserRole.USER), new Project("p1", "p1 d"));
        new Booking(usersProjects, new Date(0), new Date(1));
    }

    /**
     * It should not be possible to create {@link Booking} with an end dtae
     * before start date.
     */
    @Test
    public void endBeforeStart() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("must before");
        new Booking(createUp(), new Date(3), new Date(2));
    }

    /**
     * Creates and persists a {@link UsersProjects}. It is needed to create a
     * {@link Booking}.
     * 
     * @return
     */
    private UsersProjects createUp() {
        return upRepository.store(new UsersProjects(new User("klaus", UserRole.USER),
                                                    new Project("p1", "p1 d")));
    }
}
