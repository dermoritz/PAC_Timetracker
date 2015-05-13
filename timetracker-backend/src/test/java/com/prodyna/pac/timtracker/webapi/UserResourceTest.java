package com.prodyna.pac.timtracker.webapi;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.core.Is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.jayway.restassured.response.Response;
import com.prodyna.pac.timtracker.model.UserRole;
import com.prodyna.pac.timtracker.model.util.PersistenceArquillianContainer;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserResource;

/**
 * Tests rest api for user - {@link UserResource}
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RunWith(Arquillian.class)
public class UserResourceTest {

    private static final String USER_PATH = "timetracker/user";

    /**
     * testable is set to false because we do blackbox test. tests are conducted
     * outside container aginst rest api in container.
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
     * creates, retrieves, updates, deletes a user via rest api and xml media type.
     * @throws MalformedURLException
     */
    @Test
    public void userLifeCycleXml() throws MalformedURLException {
        userLifeCycle(MediaType.APPLICATION_XML);
    }
    
    /**
     * creates, retrieves, updates, deletes a user via rest api and json media type.
     * @throws MalformedURLException
     */
    @Test
    public void userLifeCycleJson() throws MalformedURLException {
        userLifeCycle(MediaType.APPLICATION_JSON);
    }
    
    private void userLifeCycle(String mediaType) throws MalformedURLException{
        UserRepresentation xmlUser = new UserRepresentation();
        xmlUser.setName("Klaus");
        xmlUser.setRole("USER");
        //Store user
        URL url = new URL(base, USER_PATH);
        String uriUser = given().contentType(mediaType).body(xmlUser)
                                .then().statusCode(Status.CREATED.getStatusCode())
                                .when().post(url)
                                //store should return uri for stored object in location header
                                .header("Location");
        //retrieve the user by url returned on creation
        UserRepresentation fetchedUser = given().then().contentType(mediaType).statusCode(Status.OK.getStatusCode())
               .when().get(uriUser).body().as(UserRepresentation.class);
        assertThat(fetchedUser.getName(), is("Klaus"));
        //update user
        String newRole = UserRole.ADMIN.name();
        fetchedUser.setRole(newRole);
        given().contentType(mediaType).body(fetchedUser).
        then().statusCode(Status.NO_CONTENT.getStatusCode()).when().put(uriUser);
        //check update
        UserRepresentation updatedUser = given().then().contentType(mediaType).statusCode(Status.OK.getStatusCode())
        .when().get(uriUser).body().as(UserRepresentation.class);
        assertThat(updatedUser.getRole(), is(newRole));
        //delete user
        given().then().statusCode(Status.NO_CONTENT.getStatusCode()).when().delete(uriUser);
        //now fetching user should return 404
        given().then().statusCode(Status.NOT_FOUND.getStatusCode()).when().get(uriUser);
    }

}
