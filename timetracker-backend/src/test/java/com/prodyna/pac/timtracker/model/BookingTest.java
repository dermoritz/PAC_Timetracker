package com.prodyna.pac.timtracker.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
                                              User_.class,
                                              Project_.class);
    }

    @Inject
    private EntityManager em;

    @Test
    public void should_be_deployed() {
        Booking booking = new Booking();
        booking.setStart(new Timestamp(0));
        booking.setEnd(new Timestamp(2));
        User user = new User();
        String name = "Klaus";
        user.setName(name);
        em.persist(user);
    }

    @Test
    @Cleanup(phase = TestExecutionPhase.NONE)
    public void createBooking() {
        Booking booking = new Booking();
        booking.setStart(new Timestamp(0));
        booking.setEnd(new Timestamp(2));
        User user = new User();
        String name = "Klaus";
        user.setName(name);
        em.persist(user);

        User user1 = new User();
        String name1 = "Klaus1";
        user1.setName(name1);
        em.persist(user1);

        booking.setOwner(user);
        Project project = new Project();
        String projectName = "theOne";
        project.setName(projectName);
        project.setDescription("blub");
        em.persist(project);
        booking.setProject(project);
        em.persist(booking);
        assertNotNull(booking.getId());
        assertNotNull(user.getId());
        // store all in database
        em.flush();
        // detach all from em.
        em.clear();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<User> userCriteria = cb.createQuery(User.class);
        Root<User> userR = userCriteria.from(User.class);
        userCriteria.select(userR).where(cb.equal(userR.get(User_.name), name));
        User fetchedUser = em.createQuery(userCriteria).getSingleResult();
        assertTrue(fetchedUser.getBookings().size() == 1);

        CriteriaQuery<Project> projectCriteria = cb.createQuery(Project.class);
        Root<Project> projectR = projectCriteria.from(Project.class);
        projectCriteria.select(projectR).where(cb.equal(projectR.get(Project_.name), projectName));
        Project fetchedProject = em.createQuery(projectCriteria).getSingleResult();
        assertTrue(fetchedProject.getBookings().size() == 1);

        System.out.println(fetchedUser.getName());
        System.out.println("here");
    }
}
