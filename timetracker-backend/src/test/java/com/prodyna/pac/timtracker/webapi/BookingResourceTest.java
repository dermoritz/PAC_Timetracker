package com.prodyna.pac.timtracker.webapi;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.prodyna.pac.timtracker.cdi.CurrentUserProducer;
import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.util.PersistenceArquillianContainer;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.users_projects.UsersProjectsRepresentation;
import com.prodyna.pac.timtracker.webapi.util.TestUserAdmin;

/**
 * Tests rest api for user - {@link Booking}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RunWith(Arquillian.class)
public class BookingResourceTest {

    private static final String BOOKING_PATH = "timetracker/booking";

    private static final String USERSPROJECTS_PATH = "timetracker/usersprojects";

    /**
     * testable is set to false because we do blackbox test. tests are conducted
     * outside container against rest api in container.
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
     * creates, retrieves, updates, deletes a user via rest api and xml media
     * type.
     * 
     * @throws MalformedURLException
     */
    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE)
    public void bookingLifeCycleXml() throws MalformedURLException {
        bookingLifeCycle(MediaType.APPLICATION_XML);
    }

    /**
     * creates, retrieves, updates, deletes a user via rest api and json media
     * type.
     * 
     * @throws MalformedURLException
     */
    @Test
    @Cleanup(phase = TestExecutionPhase.BEFORE)
    public void bookingLifeCycleJson() throws MalformedURLException {
        bookingLifeCycle(MediaType.APPLICATION_JSON);
    }

    private void bookingLifeCycle(String mediaType) throws MalformedURLException {
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

        // Store users project, users project must be persisted before using it
        // for a booking
        URL bookingUrl = new URL(base, BOOKING_PATH);
        URL upUrl = new URL(base, USERSPROJECTS_PATH);
        String uriUsersProject = given().contentType(mediaType).body(upRep)
                                        .then().statusCode(Status.CREATED.getStatusCode())
                                        .when().post(upUrl)
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
        assertNotNull(fetchedUP.getId());
        //
        BookingRepresentation bookingRep = new BookingRepresentation();
        Date start = new Date(3600000);
        Date end = new Date(3600000 * 2);
        bookingRep.setEnd(end);
        bookingRep.setStart(start);
        bookingRep.setUsersProjects(fetchedUP);

        String uriBooking = given().contentType(mediaType).body(bookingRep)
                                   .then().statusCode(Status.CREATED.getStatusCode())
                                   .when().post(bookingUrl)
                                   // store should return uri for stored
                                   // object in location header
                                   .header("Location");
        // fetch stored booking
        BookingRepresentation fetchedBooking = given().then().contentType(mediaType)
                                                      .statusCode(Status.OK.getStatusCode())
                                                      .when().get(uriBooking).body()
                                                      .as(BookingRepresentation.class);
        Long bookingId = fetchedBooking.getId();
        assertNotNull(fetchedBooking.getId());
        assertThat(fetchedBooking.getStart(), is(start));
        assertThat(fetchedBooking.getEnd(), is(end));
        // update
        Date newStart = new Date(0);
        Date newEnd = new Date(3600000 * 7);

        fetchedBooking.setStart(newStart);
        fetchedBooking.setEnd(newEnd);
        given().contentType(mediaType).body(fetchedBooking).then().statusCode(Status.NO_CONTENT.getStatusCode()).when()
               .put(uriBooking);

        BookingRepresentation updatedProject = given().then().contentType(mediaType)
                                                      .statusCode(Status.OK.getStatusCode())
                                                      .when().get(uriBooking).body().as(BookingRepresentation.class);
        assertThat(updatedProject.getId(), is(bookingId));
        assertThat(updatedProject.getStart(), is(newStart));
        assertThat(updatedProject.getEnd(), is(newEnd));

        // delete usersprojects
        given().then().statusCode(Status.NO_CONTENT.getStatusCode()).when().delete(uriBooking);
        // now fetching user should return 404
        given().then().statusCode(Status.NOT_FOUND.getStatusCode()).when().get(uriBooking);
    }

    @Test
    public void checkOverlapping() throws MalformedURLException {
        // create user
        UserRepresentation xmlUser = new UserRepresentation();
        String userName = "Klaus1";
        String userRole = "USER";
        xmlUser.setName(userName);
        xmlUser.setRole(userRole);
        // create project
        ProjectRepresentation projectRep = new ProjectRepresentation();
        String pDescr = "p1 d";
        String pName = "p11";
        projectRep.setDescription(pDescr);
        projectRep.setName(pName);
        // register user to project
        UsersProjectsRepresentation upRep = new UsersProjectsRepresentation();
        upRep.setProject(projectRep);
        upRep.setUser(xmlUser);

        // Store users project, users project must be persisted before using it
        // for a booking
        URL upUrl = new URL(base, USERSPROJECTS_PATH);
        String uriUsersProject = given().contentType(MediaType.APPLICATION_JSON).body(upRep)
                                        .then().statusCode(Status.CREATED.getStatusCode())
                                        .when().post(upUrl)
                                        // store should return uri for stored
                                        // object in location header
                                        .header("Location");
        // retrieve the usersprojects by url returned on creation
        UsersProjectsRepresentation fetchedUP = given().then().contentType(MediaType.APPLICATION_JSON)
                                                       .statusCode(Status.OK.getStatusCode())
                                                       .when().get(uriUsersProject).body()
                                                       .as(UsersProjectsRepresentation.class);
        assertThat(fetchedUP.getUser().getName(), is(userName));
        assertThat(fetchedUP.getProject().getDescription(), is(pDescr));
        assertNotNull(fetchedUP.getId());
        // create booking
        URL bookingUrl = new URL(base, BOOKING_PATH);
        BookingRepresentation bookingRep = new BookingRepresentation();
        Date start = new Date(2);
        Date end = new Date(3);
        bookingRep.setEnd(end);
        bookingRep.setStart(start);
        bookingRep.setUsersProjects(fetchedUP);

        String uriBooking1 = given().contentType(MediaType.APPLICATION_JSON).body(bookingRep)
                                    .then().statusCode(Status.CREATED.getStatusCode())
                                    .when().post(bookingUrl)
                                    // store should return uri for stored
                                    // object in location header
                                    .header("Location");
        // fetch stored booking
        BookingRepresentation fetchedBooking1 = given().then().contentType(MediaType.APPLICATION_JSON)
                                                       .statusCode(Status.OK.getStatusCode())
                                                       .when().get(uriBooking1).body()
                                                       .as(BookingRepresentation.class);

        BookingRepresentation bookingRep1 = new BookingRepresentation();
        Date start1 = new Date(4);
        Date end1 = new Date(6);
        bookingRep1.setEnd(end1);
        bookingRep1.setStart(start1);
        bookingRep1.setUsersProjects(fetchedUP);

        String uriBooking2 = given().contentType(MediaType.APPLICATION_JSON).body(bookingRep1)
                                    .then().statusCode(Status.CREATED.getStatusCode())
                                    .when().post(bookingUrl)
                                    // store should return uri for stored
                                    // object in location header
                                    .header("Location");
        // fetch stored booking
        BookingRepresentation fetchedBooking2 = given().then().contentType(MediaType.APPLICATION_JSON)
                                                       .statusCode(Status.OK.getStatusCode())
                                                       .when().get(uriBooking2).body()
                                                       .as(BookingRepresentation.class);
        String ovPath = BOOKING_PATH + "/overlapping/" + fetchedUP.getUser().getId();
        URL overlapping = new URL(base, ovPath + "/1/6");
        BookingRepresentation[] bookings = given().then().contentType(MediaType.APPLICATION_JSON)
                                                  .statusCode(Status.OK.getStatusCode()).when().get(overlapping).body()
                                                  .as(BookingRepresentation[].class);
        assertThat(bookings.length, is(2));
        overlapping = new URL(base, ovPath + "/1/2");
        bookings = given().then().contentType(MediaType.APPLICATION_JSON)
                .statusCode(Status.OK.getStatusCode()).when().get(overlapping).body()
                .as(BookingRepresentation[].class);
        assertThat(bookings.length, is(0));

    }
}
