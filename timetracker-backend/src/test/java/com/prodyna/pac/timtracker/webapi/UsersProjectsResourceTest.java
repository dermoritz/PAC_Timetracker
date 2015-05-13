package com.prodyna.pac.timtracker.webapi;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.prodyna.pac.timtracker.model.util.PersistenceArquillianContainer;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserResource;
import com.prodyna.pac.timtracker.webapi.resource.users_projects.UsersProjectsRepresentation;

/**
 * Tests rest api for user - {@link UserResource}
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RunWith(Arquillian.class)
public class UsersProjectsResourceTest {

    private static final String USERSPROJECTS_PATH = "timetracker/usersprojects";

    /**
     * testable is set to false because we do blackbox test. tests are conducted
     * outside container against rest api in container.
     * 
     * @return
     */
    @Deployment(testable = false)
    public static WebArchive deploy() {
        return PersistenceArquillianContainer.get().addPackages(true, "com.prodyna.pac.timtracker")
                                             .addClasses(Strings.class, Preconditions.class);
    }

    @ArquillianResource
    private URL base;

    /**
     * creates, retrieves, updates, deletes a user via rest api and xml media
     * type.
     * 
     * @throws MalformedURLException
     */
    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE)
    public void usersProjectsLifeCycleXml() throws MalformedURLException {
        usersProjectsLifeCycle(MediaType.APPLICATION_XML);
    }

    /**
     * creates, retrieves, updates, deletes a user via rest api and json media
     * type.
     * 
     * @throws MalformedURLException
     */
    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE)
    public void usersProjectsLifeCycleJson() throws MalformedURLException {
        usersProjectsLifeCycle(MediaType.APPLICATION_JSON);
    }

    private void usersProjectsLifeCycle(String mediaType) throws MalformedURLException {
        UserRepresentation xmlUser = new UserRepresentation();
        String userName = "Klaus" + mediaType;
        String userRole = "USER";
        xmlUser.setName(userName);
        xmlUser.setRole(userRole);
        ProjectRepresentation projectRep = new ProjectRepresentation();
        String pDescr = "p1 d";
        String pName = "p1" + mediaType;
        projectRep.setDescription(pDescr);
        projectRep.setName(pName);
        UsersProjectsRepresentation upRep = new UsersProjectsRepresentation();
        upRep.setProject(projectRep);
        upRep.setUser(xmlUser);

        // Store users project
        URL url = new URL(base, USERSPROJECTS_PATH);
        String uriUsersProject = given().contentType(mediaType).body(upRep)
                                        .then().statusCode(Status.CREATED.getStatusCode())
                                        .when().post(url)
                                        // store should return uri for stored
                                        // object in location header
                                        .header("Location");
        // retrieve the usersprojects by url returned on creation
        UsersProjectsRepresentation fetchedUP = given().then().contentType(mediaType)
                                                       .statusCode(Status.OK.getStatusCode())
                                                       .when().get(uriUsersProject).body()
                                                       .as(UsersProjectsRepresentation.class);
        assertThat(fetchedUP.getUser().getName(), is(userName));
        assertThat(fetchedUP.getProject().getDescription(), is(pDescr));
        // usersproject is immutable - no update

        // delete usersprojects
        given().then().statusCode(Status.NO_CONTENT.getStatusCode()).when().delete(uriUsersProject);
        // now fetching user should return 404
        given().then().statusCode(Status.NOT_FOUND.getStatusCode()).when().get(uriUsersProject);
    }

}
