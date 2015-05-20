package com.prodyna.pac.timtracker.model;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
import com.prodyna.pac.timtracker.persistence.Created;
import com.prodyna.pac.timtracker.persistence.EventRepositoryDecorator;
import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.persistence.PersistenceRepository;
import com.prodyna.pac.timtracker.persistence.Removed;
import com.prodyna.pac.timtracker.persistence.Repository;
import com.prodyna.pac.timtracker.persistence.Timestampable;
import com.prodyna.pac.timtracker.persistence.UsersProjectsCdiDelegatorRepository;

/**
 * Tests jpa interactions for {@link UsersProjects} via {@link Repository}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Transactional(TransactionMode.COMMIT)
@RunWith(Arquillian.class)
public class UsersProjectsTest {
    @Deployment
    public static WebArchive createDeployment() {
        return PersistenceArquillianContainer.addClasses(Project.class,
                                                         Project_.class,
                                                         User.class,
                                                         User_.class,
                                                         UsersProjects.class,
                                                         UserRole.class,
                                                         Repository.class,
                                                         PersistenceRepository.class,
                                                         Identifiable.class,
                                                         BaseEntity.class,
                                                         Timestampable.class,
                                                         UsersProjectsRepository.class,
                                                         Strings.class,
                                                         Preconditions.class,
                                                         UsersProjectsCdiDelegatorRepository.class,
                                                         EventRepositoryDecorator.class,
                                                         Created.class,
                                                         Removed.class,
                                                         EntityManagerProducer.class,
                                                         UserRepository.class,
                                                         ProjectRepository.class,
                                                         UsersProjects_.class,
                                                         BaseEntity_.class);
    }

    /**
     * JPA interactions will be conducted on this abstract {@link Repository}.
     */
    @Inject
    private Repository<UsersProjects> repository;

    /**
     * Used to perform queries specific to {@link UsersProjects}.
     */
    @Inject
    private UsersProjectsRepository upRepository;

    @Inject
    private UserRepository uRepo;

    @Inject
    private ProjectRepository pRepo;

    // these fields are static because Events observed by this TestClass
    // are not observed on the same TestClass instance as @Test is running.
    private static boolean createdFired = false;
    private static boolean removedFired = false;

    /**
     * Observes created events and set the flag.
     * 
     * @param project
     */
    public static void createdEventFired(@Observes @Created UsersProjects up) {
        createdFired = true;
    }

    /**
     * Observes removed event and sets a flag.
     * 
     * @param user
     */
    public static void removedEventFired(@Observes @Removed UsersProjects up) {
        removedFired = true;
    }

    @Before
    public void resetFlags() {
        createdFired = false;
        removedFired = false;
    }

    /**
     * Tests creation and removal of {@link UsersProjects}.
     */
    @Test
    public void createRemove() {

        // create
        UsersProjects usersProjects = new UsersProjects(new User("Peter", UserRole.USER), new Project("p1", "d of p1"));
        UsersProjects storedUsersProjects = repository.store(usersProjects);
        Long id = storedUsersProjects.getId();
        // id was generated
        assertNotNull(id);
        // fetch from db
        UsersProjects fetchedUsersProjects = repository.get(id);
        // fetch linked entities
        Project fetchedProject = fetchedUsersProjects.getProject();
        User fetchedUser = storedUsersProjects.getUser();
        // should work due to cascaded persist
        assertNotNull(fetchedProject.getId());
        assertNotNull(fetchedUser.getId());
        // should work due to cascaded merge
        assertThat(fetchedUser.getRole(), is(UserRole.USER));
        assertThat(fetchedProject.getDescription(), is("d of p1"));

        // remove USersProject
        repository.remove(storedUsersProjects);
        assertNull(repository.get(id));
        // since removal is not cascaded user and project should still exist
        assertNotNull(fetchedProject.getId());
        assertNotNull(fetchedUser.getId());
        assertTrue(createdFired);
        assertTrue(removedFired);
    }

    /**
     * Creates a number of {@link UsersProjects} with 2 users and queries them
     * by user. "get registered projects for user"
     */
    @Test
    public void queryByUser() {
        // create 2 users
        User user1 = uRepo.store(new User("user1", UserRole.USER));
        User user2 = uRepo.store(new User("user2", UserRole.USER));
        // create some projects
        int pCount = 12;
        Project[] projects = new Project[pCount];
        for (int i = 0; i < pCount; i++) {
            projects[i] = pRepo.store(new Project(Integer.toString(i), Integer.toString(i)));
        }
        // user 1 is registered to first 5 projects
        int user1pCount = 5;
        List<UsersProjects> user1Ups = new ArrayList<>();
        for (int i = 0; i < user1pCount; i++) {
            user1Ups.add(repository.store(new UsersProjects(user1, projects[i])));
        }
        // user 2 is registered to all projects
        List<UsersProjects> user2Ups = new ArrayList<>();
        for (int i = 0; i < pCount; i++) {
            user2Ups.add(repository.store(new UsersProjects(user2, projects[i])));
        }
        assertThat(upRepository.getByUser(user1), contains(user1Ups.toArray(new UsersProjects[] {})));
        assertThat(upRepository.getByUser(user2), contains(user2Ups.toArray(new UsersProjects[] {})));
    }
    
    /**
     * Creates a number of {@link UsersProjects} with 2 projects and queries them
     * by project. "get all users registered for given project"
     */
    @Test
    public void queryByProject() {
        // create 2 projects
        Project project1 = pRepo.store(new Project("project1", "p1"));
        Project project2 = pRepo.store(new Project("project2", "p2"));
        // create some users
        int userCount = 13;
        User[] users = new User[userCount];
        for (int i = 0; i < userCount; i++) {
            users[i] = uRepo.store(new User(Integer.toString(i), UserRole.USER));
        }
        // project 1 is registered to 7 users
        int project1uCount = 7;
        List<UsersProjects> project1Ups = new ArrayList<>();
        for (int i = 0; i < project1uCount; i++) {
            project1Ups.add(repository.store(new UsersProjects(users[i], project1)));
        }
        //project 2 is registered to all users
        List<UsersProjects> project2Ups = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            project2Ups.add(repository.store(new UsersProjects(users[i], project2)));
        }
        assertThat(upRepository.getByProject(project1), contains(project1Ups.toArray(new UsersProjects[]{})));
        assertThat(upRepository.getByProject(project2), contains(project2Ups.toArray(new UsersProjects[]{})));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Checks exception for null user.
     */
    @Test
    public void checkNullUser() {
        thrown.expect(NullPointerException.class);
        new UsersProjects(null, new Project("p8", ""));
    }

    /**
     * Checks exception for null project.
     */
    @Test
    public void checkNullProject() {
        thrown.expect(NullPointerException.class);
        new UsersProjects(new User("blub", UserRole.USER), null);
    }
}
