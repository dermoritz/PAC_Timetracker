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
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.prodyna.pac.timtracker.cdi.CurrentUserProducer;
import com.prodyna.pac.timtracker.model.util.PersistenceArquillianContainer;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.users_projects.UsersProjectsRepresentation;
import com.prodyna.pac.timtracker.webapi.util.TestUserAdmin;

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
        return PersistenceArquillianContainer.get()
                                             .addPackages(true,
                                                          Filters.exclude(CurrentUserProducer.class),
                                                          "com.prodyna.pac.timtracker")
                                             .addClasses(Strings.class, Preconditions.class, TestUserAdmin.class);
    }

    @ArquillianResource
    private URL base;

    private static boolean dataCreated = false;

    private static List<UserRepresentation> users;
    
    private static List<ProjectRepresentation> projects;

    /**
     * Creates data once for multiple tests. Bad style (accessing static fields
     * from non static method) because Arqulliean resources are not availble
     * statically without extension.
     */
    @Before
    public void createDataOnce() {
        if (!dataCreated) {
            users = createSomeUsers(20);
            projects = createSomeProjects(20);
            dataCreated = true;
        }
    }

    @Test
    public void getBookingsForProjectAndUser() throws MalformedURLException {
        // create 2 projects
        ProjectRepresentation p1 = projects.get(0);
        ProjectRepresentation p2 = projects.get(1);
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
        ProjectRepresentation project = projects.get(0);
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
        URL allProjectsUrl = new URL(base, PROJECT_PATH + "/all");
        ProjectRepresentation[] fetchedProjects = given().then().contentType(MediaType.APPLICATION_JSON)
                                                   .statusCode(Status.OK.getStatusCode()).when().get(allProjectsUrl).body()
                                                   .as(ProjectRepresentation[].class);
        assertThat(Arrays.asList(fetchedProjects), containsInAnyOrder(projects.toArray()));
    }

    /**
     * Fetches all user paginated
     * 
     * @throws MalformedURLException
     */
    @Test
    public void fetchAllPaginated() throws MalformedURLException {
        int projectCount = projects.size();
        URL allProjectsUrl = new URL(base, PROJECT_PATH + "/all");
        // fetch list via rest - select page 3 with page size 7
        int page = 3;
        int pageSize = 7;
        ProjectRepresentation[] fetchedProjects = given().queryParams(ImmutableMap.of(RepositoryResource.QUERY_PARAM_PAGE,
                                                                                page,
                                                                                RepositoryResource.QUERY_PARAM_PAGE_SIZE,
                                                                                pageSize))
                                                   .then().contentType(MediaType.APPLICATION_JSON)
                                                   .statusCode(Status.OK.getStatusCode()).when().get(allProjectsUrl).body()
                                                   .as(ProjectRepresentation[].class);
        // page 3 should contain indexes 14..19 (6 entries)
        // sublist's upper bound is exclusive - so userCount will work
        assertThat(Arrays.asList(fetchedProjects),
                   containsInAnyOrder(projects.subList(14, projectCount).toArray(new ProjectRepresentation[] {})));
    }

    @Test
    public void fetchUsersProjectsByUser() throws MalformedURLException {
        UserRepresentation user1 = createUser();
        UserRepresentation user2 = createUser();
        List<UsersProjectsRepresentation> upUser1 = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            upUser1.add(createUP(user1 , projects.get(i)));
        }
        List<UsersProjectsRepresentation> upUser2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            upUser2.add(createUP(user2 , projects.get(i)));
        }
        URL user1url = new URL(base, USERSPROJECTS_PATH + "/user/" + user1.getId());
        UsersProjectsRepresentation[] user1projects = given().then().contentType(MediaType.APPLICATION_JSON)
                                                             .statusCode(Status.OK.getStatusCode()).when()
                                                             .get(user1url).body()
                                                             .as(UsersProjectsRepresentation[].class);
        assertThat(Arrays.asList(user1projects),
                   containsInAnyOrder(upUser1.toArray(new UsersProjectsRepresentation[] {})));
        URL user2url = new URL(base, USERSPROJECTS_PATH + "/user/" + user2.getId());
        UsersProjectsRepresentation[] user2projects = given().then().contentType(MediaType.APPLICATION_JSON)
                                                             .statusCode(Status.OK.getStatusCode()).when()
                                                             .get(user2url).body()
                                                             .as(UsersProjectsRepresentation[].class);
        assertThat(Arrays.asList(user2projects),
                   containsInAnyOrder(upUser2.toArray(new UsersProjectsRepresentation[] {})));
    }

    @Test
    public void fetchUsersProjectsByProject() throws MalformedURLException {
        //use different projects than in fetchUsersProjectsByUser
        ProjectRepresentation p1 = projects.get(17);
        ProjectRepresentation p2 = projects.get(18);

        List<UsersProjectsRepresentation> upP1 = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            upP1.add(createUP(createUser(), p1));
        }
        List<UsersProjectsRepresentation> upP2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            upP2.add(createUP(createUser(), p2));
        }
        URL user1url = new URL(base, USERSPROJECTS_PATH + "/project/" + p1.getId());
        UsersProjectsRepresentation[] p1projects = given().then().contentType(MediaType.APPLICATION_JSON)
                                                          .statusCode(Status.OK.getStatusCode()).when()
                                                          .get(user1url).body()
                                                          .as(UsersProjectsRepresentation[].class);
        assertThat(Arrays.asList(p1projects),
                   containsInAnyOrder(upP1.toArray(new UsersProjectsRepresentation[] {})));
        URL user2url = new URL(base, USERSPROJECTS_PATH + "/project/" + p2.getId());
        UsersProjectsRepresentation[] p2projects = given().then().contentType(MediaType.APPLICATION_JSON)
                                                          .statusCode(Status.OK.getStatusCode()).when()
                                                          .get(user2url).body()
                                                          .as(UsersProjectsRepresentation[].class);
        assertThat(Arrays.asList(p2projects),
                   containsInAnyOrder(upP2.toArray(new UsersProjectsRepresentation[] {})));
    }

    private List<UserRepresentation> createSomeUsers(int userCount) {
        List<UserRepresentation> result = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            result.add(createUser());
        }
        return result;
    }
    
    private List<ProjectRepresentation> createSomeProjects(int projectCount) {
        List<ProjectRepresentation> result = new ArrayList<>();
        for (int i = 0; i < projectCount; i++) {
            result.add(createProject());
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
