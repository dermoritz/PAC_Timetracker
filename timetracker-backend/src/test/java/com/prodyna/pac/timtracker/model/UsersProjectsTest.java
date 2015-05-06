package com.prodyna.pac.timtracker.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import com.prodyna.pac.timtracker.model.util.ArquillianContainer;
import com.prodyna.pac.timtracker.persistence.BaseEntity;
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
        return ArquillianContainer.addClasses(Project.class,
                                              User.class,
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
                                              EntityManagerProducer.class);
    }

    /**
     * JPA interactions will be conducted on this abstract {@link Repository}.
     */
    @Inject
    private Repository<UsersProjects> repository;

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
        //create
        UsersProjects usersProjects = new UsersProjects(new User("Peter", UserRole.USER), new Project("p1", "d of p1"));
        UsersProjects storedUsersProjects = repository.store(usersProjects);
        Long id = storedUsersProjects.getId();
        assertNotNull(id);
        UsersProjects fetchedUsersProjects = repository.get(id);
        Long projectId = fetchedUsersProjects.getProject().getId();
        Long userId = fetchedUsersProjects.getUser().getId();
        //should work due to cascaded persist
        assertNotNull(projectId);
        assertNotNull(userId);
        //remove USersProject
        repository.remove(storedUsersProjects);
        assertNull(repository.get(id));
        //since removal is not cascaded user and project should still exist
        assertNotNull(projectId);
        assertNotNull(userId);        
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
