package com.prodyna.pac.timtracker.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import com.prodyna.pac.timtracker.cdi.EntityManagerProducer;

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
                                              UserProjects.class,
                                              UserProjectKey.class);
    }

    @Inject
    private EntityManager em;

    @Cleanup(phase = TestExecutionPhase.BEFORE)
    @Test
    public void persistBooking() {
        // create a booking
        Booking booking = new Booking();
        booking.setStart(new Timestamp(0));
        booking.setEnd(new Timestamp(2));
        // create a user - booking needs user/owner
        User user = new User();
        String name = "Klaus";
        user.setName(name);
        em.persist(user);

        // create project, booking needs project
        Project project = new Project();
        String projectName = "theOne";
        project.setName(projectName);
        project.setDescription("blub");
        em.persist(project);
        //register user to project
        UserProjects userProject = new UserProjects(user, project);
        em.persist(userProject);
        //
        booking.setUserProject(userProject);
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
    
    @Test
    public void noUserProject() {
        // create a booking
        Booking booking = new Booking();
        booking.setStart(new Timestamp(0));
        booking.setEnd(new Timestamp(2));
        // validate
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Booking>> validations = validator.validate(booking);
        assertThat(validations.size(), is(1));
        for (ConstraintViolation<Booking> violation : validations) {
            assertTrue("Each validation message should contain \"null\". I got \"" + violation.getMessage() + "\"",
                       violation.getMessage().contains("null"));
        }
    }

    @Test
    public void endBeforStart() {
        // create a booking
        Booking booking = new Booking();
        // set end before start
        booking.setStart(new Timestamp(5));
        booking.setEnd(new Timestamp(2));
        // create a user - booking needs user/owner
        User user = new User();
        String name = "Klaus";
        user.setName(name);
        // create project, booking needs project
        Project project = new Project();
        String projectName = "theOne";
        project.setName(projectName);
        project.setDescription("blub");
        
        UserProjects userProjects = new UserProjects(user, project);
        booking.setUserProject(userProjects);
        
        // validate
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Booking>> validations = validator.validate(booking);
        assertThat(validations.size(), is(1));
    }
}
