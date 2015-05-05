package com.prodyna.pac.timtracker.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.prodyna.pac.timtracker.cdi.EntityManagerProducer;
import com.prodyna.pac.timtracker.model.util.ArquillianContainer;

@RunWith(Arquillian.class)
@Transactional
public class BookingTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ArquillianContainer.addClasses(EntityManagerProducer.class,
                                              Booking.class,
                                              User.class,
                                              Project.class,
                                              UserRole.class,
                                              Booking_.class,
                                              UsersProjects.class,
                                              UsersProjects.class,
                                              Preconditions.class,
                                              Strings.class);
    }

    @Inject
    private EntityManager em;

    @Cleanup(phase = TestExecutionPhase.BEFORE)
    @Test
    public void persistBooking() {

        // create a user - booking needs user/owner
        String name = "Klaus";
        User user = new User(name, UserRole.USER);
        em.persist(user);

        // create project, booking needs project
        Project project = new Project("theOne", "blub");
        em.persist(project);
        // register user to project
        UsersProjects userProject = new UsersProjects(user, project);
        em.persist(userProject);

        // create a booking
        Booking booking = new Booking(userProject, new Date(0), new Date(1));
        // perists booking
        em.persist(booking);

        // check ids
        assertNotNull(user);
        Long bookingId = booking.getId();
        assertNotNull(bookingId);
        assertNotNull(user.getId());
        // store all in database
        em.flush();
        // detach all from em.
        em.clear();

        // now read data from db
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // fetch booking from db
        CriteriaQuery<Booking> bookingCriteria = cb.createQuery(Booking.class);
        Root<Booking> bookingRoot = bookingCriteria.from(Booking.class);
        bookingCriteria.select(bookingRoot).where(
                                                  cb.equal(bookingRoot.get(Booking_.id), bookingId));
        Booking fetchedBooking = em.createQuery(bookingCriteria)
                                   .getSingleResult();
        User fetchedOwner = fetchedBooking.getOwner();
        Project fetchedProject = fetchedBooking.getProject();
        // assert based on equals
        assertThat(fetchedBooking, is(booking));
        assertThat(fetchedOwner, is(user));
        assertThat(fetchedProject, is(project));

    }

    // Validation tests
    
    /**
     * Rule to check expected exceptions. Rules must be public.
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Check if validation failse due to nos {@link UsersProjects} set for
     * booking.
     */
    @Test
    public final void noUserProject() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("userProject must not be null");
        new Booking(null, new Date(1), new Date(2));
    }

    /**
     * Check if validation fails due to end date is before start.
     */
    @Test
    public final void endBeforeStart() {
        String name = "Klaus";
        User user = new User(name, UserRole.USER);
        // create project, booking needs project
        Project project = new Project("theOne","blub");
        // register user to project
        UsersProjects userProject = new UsersProjects(user, project);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Start date must before end date.");
        new Booking(userProject, new Date(5), new Date(4));
    }
}
