package com.prodyna.pac.timtracker.webapi;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.prodyna.pac.timtracker.model.util.PersistenceArquillianContainer;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.users_projects.UsersProjectsRepresentation;

/**
 * This concentrates all tests about fetching/ querying different kinds of data.
 * This test crosses different resource types and presumes passing of all
 * resource tests.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RunWith(Arquillian.class)
public class FetchingDataTest {

    private static final String PROJECT_PATH = "timetracker/project";

    private static final String USER_PATH = "timetracker/user";

    private static final String USERSPROJECTS_PATH = "timetracker/usersprojects";

    private static final String BOOKING_PATH = "timetracker/booking";

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

    private static boolean dataCreated = false;

    private static List<UserRepresentation> users;

    /**
     * Creates data once for multiple tests. Bad style (accessing static fields
     * from non static method) because Arqulliean resources are not availble
     * statically without extension.
     */
    @Before
    public void createDataOnce() {
        if (!dataCreated) {
            users = createSomeUsers(20);
            dataCreated = true;
        }
    }

    @Test
    public void getBookingsForProjectAndUser() throws MalformedURLException {
        // create 2 projects
        ProjectRepresentation p1 = createProject();
        ProjectRepresentation p2 = createProject();
        UserRepresentation u = users.get(0);
        UsersProjectsRepresentation up1 = createUP(u, p1);
        UsersProjectsRepresentation up2 = createUP(u, p2);
        // create some bookings for p1
        List<BookingRepresentation> p1Bookings = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            p1Bookings.add(createBooking(up1));
        }
        // create some bookings for p2
        List<BookingRepresentation> p2Bookings = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            p2Bookings.add(createBooking(up2));
        }

        // fetch bookings for project
        URL bookingForProjectUrlUrl = new URL(base, PROJECT_PATH + "/" + p1.getId() + "/bookings");
        BookingRepresentation[] fetchedBookings = given().then().contentType(MediaType.APPLICATION_JSON)
                                                         .statusCode(Status.OK.getStatusCode()).when()
                                                         .get(bookingForProjectUrlUrl).body()
                                                         .as(BookingRepresentation[].class);
        assertThat(Arrays.asList(fetchedBookings), containsInAnyOrder(p1Bookings.toArray()));
        // fetch bookings for user
        URL bookingsForUserUrl = new URL(base, USER_PATH + "/" + u.getId() + "/bookings");
        BookingRepresentation[] fetchedUserBookings = given().then().contentType(MediaType.APPLICATION_JSON)
                                                             .statusCode(Status.OK.getStatusCode()).when()
                                                             .get(bookingsForUserUrl).body()
                                                             .as(BookingRepresentation[].class);
        p1Bookings.addAll(p2Bookings);
        assertThat(Arrays.asList(fetchedUserBookings), containsInAnyOrder(p1Bookings.toArray()));
    }

    @Test
    public void getProjectByName() throws MalformedURLException {
        ProjectRepresentation project = createProject();
        URL url = new URL(base, PROJECT_PATH + "/name/" + project.getName());
        ProjectRepresentation fetchedProject = given().then().contentType(MediaType.APPLICATION_JSON)
                                                      .statusCode(Status.OK.getStatusCode()).when()
                                                      .get(url).body()
                                                      .as(ProjectRepresentation.class);
        assertThat(fetchedProject, is(project));
    }

    @Test
    public void getUserByName() throws MalformedURLException {
        UserRepresentation user = users.get(0);
        URL url = new URL(base, USER_PATH + "/name/" + user.getName());
        UserRepresentation fetchedProject = given().then().contentType(MediaType.APPLICATION_JSON)
                                                      .statusCode(Status.OK.getStatusCode()).when()
                                                      .get(url).body()
                                                      .as(UserRepresentation.class);
        assertThat(fetchedProject, is(user));
    }

    /**
     * Tests fetching all users.
     * 
     * @throws MalformedURLException
     */
    @Test
    public void fetchAll() throws MalformedURLException {
        // fetch list via rest
        URL allUserUrl = new URL(base, USER_PATH + "/all");
        UserRepresentation[] fetchedUsers = given().then().contentType(MediaType.APPLICATION_JSON)
                                                   .statusCode(Status.OK.getStatusCode()).when().get(allUserUrl).body()
                                                   .as(UserRepresentation[].class);
        assertThat(Arrays.asList(fetchedUsers), containsInAnyOrder(users.toArray()));
    }

    /**
     * Fetches all user paginated
     * 
     * @throws MalformedURLException
     */
    @Test
    public void fetchAllPaginated() throws MalformedURLException {
        int userCount = users.size();
        URL allUserUrl = new URL(base, USER_PATH + "/all");
        // fetch list via rest - select page 3 with page size 7
        int page = 3;
        int pageSize = 7;
        UserRepresentation[] fetchedUsers = given().queryParams(ImmutableMap.of(RepositoryResource.QUERY_PARAM_PAGE,
                                                                                page,
                                                                                RepositoryResource.QUERY_PARAM_PAGE_SIZE,
                                                                                pageSize))
                                                   .then().contentType(MediaType.APPLICATION_JSON)
                                                   .statusCode(Status.OK.getStatusCode()).when().get(allUserUrl).body()
                                                   .as(UserRepresentation[].class);
        // page 3 should contain indexes 14..19 (6 entries)
        // sublist's upper bound is exclusive - so userCount will work
        assertThat(Arrays.asList(fetchedUsers),
                   containsInAnyOrder(users.subList(14, userCount).toArray(new UserRepresentation[] {})));
    }
    
    @Test
    public void fetchUsersProjectsByUser(){
        UserRepresentation user1 = createUser();
        UserRepresentation user2 = createUser();
        
    }
    
    private List<UserRepresentation> createSomeUsers(int userCount) {
        List<UserRepresentation> result = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            result.add(createUser());
        }
        return result;
    }
    
    /**
     * creates and stores a random project (name is uuid)
     * 
     * @return the stored project
     */
    private ProjectRepresentation createProject() {
        ProjectRepresentation project = new ProjectRepresentation();
        project.setName(UUID.randomUUID().toString());
        project.setDescription("d");
        URL url;
        try {
            url = new URL(base, PROJECT_PATH);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("wtf: ", e);
        }
        String uriProject = given().contentType(MediaType.APPLICATION_JSON).body(project)
                                   .then().statusCode(Status.CREATED.getStatusCode())
                                   .when().post(url)
                                   // store should return uri for stored object
                                   // in location header
                                   .header("Location");
        return given().then().contentType(MediaType.APPLICATION_JSON).statusCode(Status.OK.getStatusCode()).when()
                      .get(uriProject)
                      .body().as(ProjectRepresentation.class);
    }

    private UserRepresentation createUser() {
        UserRepresentation xmlUser = new UserRepresentation();
        xmlUser.setName(UUID.randomUUID().toString());
        xmlUser.setRole("USER");
        // Store user
        URL url;
        try {
            url = new URL(base, USER_PATH);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("wtf: ", e);
        }
        String uriUser = given().contentType(MediaType.APPLICATION_JSON).body(xmlUser)
                                .then().statusCode(Status.CREATED.getStatusCode())
                                .when().post(url)
                                // store should return uri for stored object in
                                // location header
                                .header("Location");
        // retrieve the user by url returned on creation
        return given().then().contentType(MediaType.APPLICATION_JSON).statusCode(Status.OK.getStatusCode())
                      .when().get(uriUser).body().as(UserRepresentation.class);
    }

    private UsersProjectsRepresentation createUP(UserRepresentation user, ProjectRepresentation project) {
        UsersProjectsRepresentation upRep = new UsersProjectsRepresentation();
        upRep.setProject(project);
        upRep.setUser(user);

        // Store users project
        URL url;
        try {
            url = new URL(base, USERSPROJECTS_PATH);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("wtf: ", e);
        }
        String uriUsersProject = given().contentType(MediaType.APPLICATION_JSON).body(upRep)
                                        .then().statusCode(Status.CREATED.getStatusCode())
                                        .when().post(url)
                                        // store should return uri for stored
                                        // object in location header
                                        .header("Location");
        // retrieve the usersprojects by url returned on creation
        return given().then().contentType(MediaType.APPLICATION_JSON)
                      .statusCode(Status.OK.getStatusCode())
                      .when().get(uriUsersProject).body()
                      .as(UsersProjectsRepresentation.class);
    }

    private BookingRepresentation createBooking(UsersProjectsRepresentation up) {
        URL bookingUrl;
        try {
            bookingUrl = new URL(base, BOOKING_PATH);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("wtf: ", e);
        }
        BookingRepresentation bookingRep = new BookingRepresentation();
        Date start = new Date(3600000);
        Date end = new Date(3600000 * 2);
        bookingRep.setEnd(end);
        bookingRep.setStart(start);
        bookingRep.setUsersProjects(up);

        String uriBooking = given().contentType(MediaType.APPLICATION_JSON).body(bookingRep)
                                   .then().statusCode(Status.CREATED.getStatusCode())
                                   .when().post(bookingUrl)
                                   // store should return uri for stored
                                   // object in location header
                                   .header("Location");
        // fetch stored booking
        return given().then().contentType(MediaType.APPLICATION_JSON)
                      .statusCode(Status.OK.getStatusCode())
                      .when().get(uriBooking).body()
                      .as(BookingRepresentation.class);
    }

}
