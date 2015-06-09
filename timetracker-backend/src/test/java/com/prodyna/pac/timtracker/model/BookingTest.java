package com.prodyna.pac.timtracker.model;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.prodyna.pac.timtracker.persistence.BaseEntity_;
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
                                                         Project_.class,
                                                         User.class,
                                                         User_.class,
                                                         UsersProjects.class,
                                                         UsersProjects_.class,
                                                         Booking.class,
                                                         Booking_.class,
                                                         UserRole.class,
                                                         BookingRepository.class,
                                                         BookingCdiDelegatorRepository.class,
                                                         UsersProjectsRepository.class,
                                                         UsersProjectsCdiDelegatorRepository.class,
                                                         Repository.class,
                                                         PersistenceRepository.class,
                                                         Identifiable.class,
                                                         BaseEntity.class,
                                                         BaseEntity_.class,
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
    
    /**
     * Special repo for booking needed for queries.
     */
    @Inject 
    private BookingRepository bRepo;

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
     * Creates booking of 2 diffrent user, and checks fetching by user.
     */
    @Test
    public void fetchByUser() {
        UsersProjects up1 = upRepository.store(new UsersProjects(new User("user1" + (new Date()).getTime(), UserRole.USER),
                                                                 new Project("project 1" + (new Date()).getTime(), "p1")));
        UsersProjects up2 = upRepository.store(new UsersProjects(new User("user2" + (new Date()).getTime(), UserRole.USER),
                                                                 new Project("project 2" + (new Date()).getTime(), "p2")));
        User user1 = up1.getUser();
        User user2 = up2.getUser();
        Project project2 = up2.getProject();
        // register user 1 also to project 2
        UsersProjects up3 = upRepository.store(new UsersProjects(user1, project2));
        // create bookings
        List<Booking> up1bookings = new ArrayList<>();
        List<Booking> up2bookings = new ArrayList<>();
        List<Booking> up3bookings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            up1bookings.add(repository.store(new Booking(up1, new Date(i), new Date(i + 10000))));
            up2bookings.add(repository.store(new Booking(up2, new Date(i), new Date(i + 10000))));
            up3bookings.add(repository.store(new Booking(up3, new Date(i), new Date(i + 10000))));
        }
        //all bookings for user 1 now in up1bookings
        up1bookings.addAll(up3bookings);
        assertThat(bRepo.getBookingsByUser(user1), contains(up1bookings.toArray(new Booking[]{})));
        assertThat(bRepo.getBookingsByUser(user2), contains(up2bookings.toArray(new Booking[]{})));
    }

    /**
     * Creates booking of 2 diffrent user, and checks fetching by user.
     */
    @Test
    public void fetchByProject() {
        UsersProjects up1 = upRepository.store(new UsersProjects(new User("user1" + (new Date()).getTime(), UserRole.USER),
                                                                 new Project("project 1" + (new Date()).getTime(), "p1")));
        UsersProjects up2 = upRepository.store(new UsersProjects(new User("user2" + (new Date()).getTime(), UserRole.USER),
                                                                 new Project("project 2" + (new Date()).getTime(), "p2")));
        User user1 = up1.getUser();
        Project project1 = up1.getProject();
        Project project2 = up2.getProject();
        // register user 1 also to project 2
        UsersProjects up3 = upRepository.store(new UsersProjects(user1, project2));
        // create bookings
        List<Booking> up1bookings = new ArrayList<>();
        List<Booking> up2bookings = new ArrayList<>();
        List<Booking> up3bookings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            up1bookings.add(repository.store(new Booking(up1, new Date(i), new Date(i + 10000))));
            up2bookings.add(repository.store(new Booking(up2, new Date(i), new Date(i + 10000))));
            up3bookings.add(repository.store(new Booking(up3, new Date(i), new Date(i + 10000))));
        }
        up2bookings.addAll(up3bookings);
        //all bookings for project 2 now in up2bookings
        assertThat(bRepo.getBookingsByProject(project1), contains(up1bookings.toArray(new Booking[]{})));
        assertThat(bRepo.getBookingsByProject(project2), contains(up2bookings.toArray(new Booking[]{})));
    }
  
    @Test
    public void fetchByProjectId() {
        UsersProjects up1 = upRepository.store(new UsersProjects(new User("user1" + (new Date()).getTime(), UserRole.USER),
                                                                 new Project("project 1" + (new Date()).getTime(), "p1")));
        UsersProjects up2 = upRepository.store(new UsersProjects(new User("user2" + (new Date()).getTime(), UserRole.USER),
                                                                 new Project("project 2" + (new Date()).getTime(), "p2")));
        User user1 = up1.getUser();
        Project project1 = up1.getProject();
        Project project2 = up2.getProject();
        // register user 1 also to project 2
        UsersProjects up3 = upRepository.store(new UsersProjects(user1, project2));
        // create bookings
        List<Booking> up1bookings = new ArrayList<>();
        List<Booking> up2bookings = new ArrayList<>();
        List<Booking> up3bookings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            up1bookings.add(repository.store(new Booking(up1, new Date(i), new Date(i + 10000))));
            up2bookings.add(repository.store(new Booking(up2, new Date(i), new Date(i + 10000))));
            up3bookings.add(repository.store(new Booking(up3, new Date(i), new Date(i + 10000))));
        }
        up2bookings.addAll(up3bookings);
        //all bookings for project 2 now in up2bookings
        List<Booking> up1fetchedBookings = bRepo.getBookingsByProjectId(project1.getId());
        List<Booking> up2fetchedBookings = bRepo.getBookingsByProjectId(project2.getId());
        assertThat(up1fetchedBookings, contains(up1bookings.toArray(new Booking[]{})));
        assertThat(up2fetchedBookings, contains(up2bookings.toArray(new Booking[]{})));
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
    
    @Test
    public void checkOverlapping() {
        UsersProjects storedUp = createUp();
        //create 2 bookings
        Booking b1 = repository.store(new Booking(storedUp, new Date(2), new Date(3)));
        Booking b2 = repository.store(new Booking(storedUp, new Date(4), new Date(6)));
        //check non overlapping
        List<Booking> overlapping = bRepo.getOverlapping(storedUp.getUser().getId(), new Date(6), new Date(8));
        assertThat(overlapping.size(), is(0));
        overlapping = bRepo.getOverlapping(storedUp.getUser().getId(), new Date(1), new Date(2));
        assertThat(overlapping.size(), is(0));
        overlapping = bRepo.getOverlapping(storedUp.getUser().getId(), new Date(3), new Date(4));
        assertThat(overlapping.size(), is(0));
        //check overlapping
        overlapping = bRepo.getOverlapping(storedUp.getUser().getId(), new Date(1), new Date(6));
        assertThat(overlapping.size(), is(2));
        overlapping = bRepo.getOverlapping(storedUp.getUser().getId(), new Date(1), new Date(3));
        assertThat(overlapping.size(), is(1));
        assertThat(overlapping.get(0), is(b1));
        overlapping = bRepo.getOverlapping(storedUp.getUser().getId(), new Date(5), new Date(8));
        assertThat(overlapping.size(), is(1));
        assertThat(overlapping.get(0), is(b2));
    }
    
    /**
     * Creates and persists a {@link UsersProjects}. It is needed to create a
     * {@link Booking}.
     * 
     * @return
     */
    private UsersProjects createUp() {
        long time = (new Date()).getTime();
        return upRepository.store(new UsersProjects(new User("klaus" + time, UserRole.USER),
                                                    new Project("p1" + time, "p1 d")));
    }
}
