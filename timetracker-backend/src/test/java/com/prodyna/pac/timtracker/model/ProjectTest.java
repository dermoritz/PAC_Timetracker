package com.prodyna.pac.timtracker.model;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
import com.prodyna.pac.timtracker.persistence.ProjectCdiDelegatorRepository;
import com.prodyna.pac.timtracker.persistence.Removed;
import com.prodyna.pac.timtracker.persistence.Repository;
import com.prodyna.pac.timtracker.persistence.Timestampable;

@Transactional(TransactionMode.COMMIT)
@RunWith(Arquillian.class)
public class ProjectTest {

    @Deployment
    public static WebArchive createDeployment() {
        return PersistenceArquillianContainer.addClasses(Project.class,
                                                         Project_.class,
                                                         BaseEntity_.class,
                                                         Repository.class,
                                                         PersistenceRepository.class,
                                                         Identifiable.class,
                                                         BaseEntity.class,
                                                         Timestampable.class,
                                                         ProjectRepository.class,
                                                         Strings.class,
                                                         Preconditions.class,
                                                         ProjectCdiDelegatorRepository.class,
                                                         EventRepositoryDecorator.class,
                                                         Created.class,
                                                         Removed.class,
                                                         EntityManagerProducer.class);
    }

    /**
     * JPA interactions will be conducted on this abstract {@link Repository}.
     */
    @Inject
    private Repository<Project> repository;

    /**
     * Specific repo that provides project specific queries.
     */
    @Inject
    private ProjectRepository projectRepository;

    // these fields are static because Events observed by this TestClass
    // are not observed on the same TestClass instance as @Test is running.
    private static boolean createdFired = false;
    private static boolean removedFired = false;

    /**
     * Observes created events and set the flag.
     * 
     * @param project
     */
    public static void createdEventFired(@Observes @Created Project project) {
        createdFired = true;
    }

    /**
     * Observes removed event and sets a flag.
     * 
     * @param user
     */
    public static void removedEventFired(@Observes @Removed Project project) {
        removedFired = true;
    }

    @Before
    public void resetFlags() {
        createdFired = false;
        removedFired = false;
    }

    /**
     * Tests store and removal of projects.
     */
    @Test
    public void storeAndRemoveProject() {
        Project storedProject = repository.store(new Project("p1", "p1's description"));
        assertTrue(createdFired);
        Long id = storedProject.getId();
        assertNotNull(id);
        Project fetchedProject = repository.get(id);
        assertNotNull(fetchedProject);
        repository.remove(fetchedProject);
        assertTrue(removedFired);
        assertNull(repository.get(id));
    }

    /**
     * Tests setter for description.
     */
    @Test
    public void setDescription() {
        Project storedProject = repository.store(new Project("p2", "p2"));
        Long id = storedProject.getId();
        assertNotNull(id);
        String newDescription = "blubber";
        storedProject.setDescription(newDescription);
        assertThat(storedProject.getDescription(), is(newDescription));
    }

    /**
     * Tests query by name.
     */
    @Test
    public void queryByName() {
        String name = "pröjäkt";
        Project storedProject = repository.store(new Project(name, name));
        Long id = storedProject.getId();
        assertNotNull(id);
        Project byName = projectRepository.getByName(name);
        assertNotNull(byName);
    }

    /**
     * Tests query all and paginated query all. Since this is implemented in
     * {@link ProjectRepository} this test covers all code for it (no need to
     * test it for other entities).
     */
    @Test
    public void queryAll() {
        int count = 100;
        for (int i = 0; i < count; i++) {
            Project storedProject = repository.store(new Project(Integer.toString(i), Integer.toString(i)));
            assertNotNull(storedProject.getId());
        }
        assertThat(repository.getAll().size(), is(count));
        // should contain projects 10 to 19 (first page is 0 to 9, last 90 to
        // 99)
        List<String> projectNameStrings = new ArrayList<>();
        List<Project> page2of10 = repository.getAllPaginated(2, 10);
        for (Project project : page2of10) {
            projectNameStrings.add(project.getName());
        }
        for (int k = 10; k <= 19; k++) {
            assertTrue(projectNameStrings.contains(Integer.toString(k)));
        }

    }

    /**
     * Rule to check exceptions.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Checks exception for empty name;
     */
    @Test
    public void emptyName() {
        thrown.expect(IllegalArgumentException.class);
        new Project("", "");
    }

    /**
     * Checks exception for null name.
     */
    @Test
    public void nullName() {
        thrown.expect(IllegalArgumentException.class);
        new Project(null, "");
    }

    /**
     * Checks exception on storing 2 projects with same name.
     */
    @Test
    public void duplicateName() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Unable to commit the transaction.");
        String name = "name";
        Project p1 = new Project(name, "");
        Project p2 = new Project(name, "p2");
        repository.store(p1);
        repository.store(p2);
    }
}
