package com.prodyna.pac.timtracker.webapi;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.prodyna.pac.timtracker.cdi.CurrentUserProducer;
import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.model.util.PersistenceArquillianContainer;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.users_projects.UsersProjectsRepresentation;
import com.prodyna.pac.timtracker.webapi.util.TestUserAdmin;

/**
 * Tests rest api for project - {@link Project}
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RunWith(Arquillian.class)
public class ProjectResourceTest {

    private static final String PROJECT_PATH = "timetracker/project";

    /**
     * testable is set to false because we do blackbox test. tests are conducted
     * outside container aginst rest api in container.
     * 
     * @return
     */
    @Deployment(testable = false)
    public static WebArchive deploy() {
        return PersistenceArquillianContainer.get()
                                             .addPackages(true,
                                                          Filters.exclude(CurrentUserProducer.class),
                                                          "com.prodyna.pac.timtracker")
                                             .addClasses(Strings.class, Preconditions.class, TestUserAdmin.class);
    }

    @ArquillianResource
    private URL base;

    /**
     * creates, retrieves, updates, deletes a project via rest api and xml media
     * type.
     * 
     * @throws MalformedURLException
     */
    @Test
    public void projectLifeCycleXml() throws MalformedURLException {
        projectLifeCycle(MediaType.APPLICATION_XML);
    }

    /**
     * creates, retrieves, updates, deletes a user via rest api and json media
     * type.
     * 
     * @throws MalformedURLException
     */
    @Test
    public void projectLifeCycleJson() throws MalformedURLException {
        projectLifeCycle(MediaType.APPLICATION_JSON);
    }

    private void projectLifeCycle(String mediaType) throws MalformedURLException {
        ProjectRepresentation projectRep = new ProjectRepresentation();
        String pDescr = "blub";
        String pName = "P1";
        projectRep.setDescription(pDescr);
        projectRep.setName(pName);
        // Store project
        URL url = new URL(base, PROJECT_PATH);
        String uriProject = given().contentType(mediaType).body(projectRep)
                                   .then().statusCode(Status.CREATED.getStatusCode())
                                   .when().post(url)
                                   // store should return uri for stored object
                                   // in location header
                                   .header("Location");
        // retrieve the project by url returned on creation
        ProjectRepresentation fetchedProject = given().then().contentType(mediaType)
                                                      .statusCode(Status.OK.getStatusCode())
                                                      .when().get(uriProject).body().as(ProjectRepresentation.class);
        assertThat(fetchedProject.getName(), is(pName));
        assertThat(fetchedProject.getDescription(), is(pDescr));
        // update project
        String newDescr = "newBlub";
        fetchedProject.setDescription(newDescr);
        given().contentType(mediaType).body(fetchedProject).
               then().statusCode(Status.NO_CONTENT.getStatusCode()).when().put(uriProject);
        // check update
        ProjectRepresentation updatedProject = given().then().contentType(mediaType)
                                                      .statusCode(Status.OK.getStatusCode())
                                                      .when().get(uriProject).body().as(ProjectRepresentation.class);
        assertThat(updatedProject.getDescription(), is(newDescr));
        // delete project
        given().then().statusCode(Status.NO_CONTENT.getStatusCode()).when().delete(uriProject);
        // now fetching project should return 404
        given().then().statusCode(Status.NOT_FOUND.getStatusCode()).when().get(uriProject);
    }

}
