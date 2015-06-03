package com.prodyna.pac.timtracker.webapi.resource.booking;

import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import com.prodyna.pac.timtracker.cdi.CurrentUser;
import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.BookingRepository;
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRole;
import com.prodyna.pac.timtracker.persistence.Repository;
import com.prodyna.pac.timtracker.webapi.RepositoryResource;
import com.prodyna.pac.timtracker.webapi.resource.users_projects.UsersProjectsRepresentation;

@Path("/booking")
@RequestScoped
public class BookingResource extends RepositoryResource<Booking, BookingRepresentation> {

    @Inject
    @CurrentUser
    private User currentUser;

    @Inject
    private Logger log;

    @Inject
    private BookingRepository bookingRepo;

    private static final String MEDIA_SUBTYPE = "; type=booking";
    
    /**
     * Creates {@link RepositoryResource} typed for user.
     */
    public BookingResource() {
        super(BookingResource.class, Booking.class, BookingRepresentation.class);
    }
    

    @Override
    protected String getMediaSupType() {
        return MEDIA_SUBTYPE;
    }


    /**
     * 
     * @param userId
     *            id of user
     * @return bookings for given user.
     */
    @GET
    @Path("/user/{user_id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getBookingsByUser(@PathParam("user_id") Long userId) {
        if (userId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        // security check - either request for own bookings or manager or admin
        if (!ownManagerOrAdmin(userId)) {
            log.debug("Rejected request by " + currentUser + " to get bookings by user for id " + userId);
            return Response.status(Status.FORBIDDEN).build();
        }
        Collection<BookingRepresentation> bookings = getConverter().from(getUriInfo(),
                                                                         bookingRepo.getBookingsByUserId(userId));
        return Response.ok(new GenericEntity<Collection<BookingRepresentation>>(bookings) {/**/
        }).type(getResourceMediaType()).build();
    }

    /**
     * 
     * @param projectId
     * @return bookings for given project.
     */
    @GET
    @Path("/project/{project_id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getBookingsByProject(@PathParam("project_id") Long projectId) {
        if (projectId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        // security check - either manager or admin
        if (!currentUser.getRole().equals(UserRole.ADMIN)
            || !currentUser.getRole().equals(UserRole.MANAGER)) {
            log.debug("Rejected request by " + currentUser + " to get bookings by project for id " + projectId);
            return Response.status(Status.FORBIDDEN).build();
        }
        Collection<BookingRepresentation> bookings = getConverter().from(getUriInfo(),
                                                                         bookingRepo.getBookingsByProjectId(projectId));
        return Response.ok(new GenericEntity<Collection<BookingRepresentation>>(bookings) {/**/
        }).type(getResourceMediaType()).build();
    }

    @Override
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(BookingRepresentation representation) {
        // current user shall only create booking for himself - or is admin
        if (!representation.getUsersProjects().getUser().getId().equals(currentUser.getId())
            && !currentUser.getRole().equals(UserRole.ADMIN)) {
            log.debug("Rejected creation of booking " + representation + " because current user is " + currentUser
                      + ".");
            return Response.status(Status.FORBIDDEN).build();
        }
        return super.create(representation);
    }

    @Override
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        // current user is only allowed to delete own bookings
        Booking booking = bookingRepo.get(id);
        // null check with 404 response is done by super
        if (booking != null && !booking.getUserProject().getUser().getId().equals(currentUser.getId())
            && !currentUser.getRole().equals(UserRole.ADMIN)) {
            log.debug("Rejected deletion of " + booking + " requested by " + currentUser + " - not owner of booking.");
            return Response.status(Status.FORBIDDEN).build();
        }
        return super.delete(id);
    }

    @Override
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response get(@PathParam("id") Long id) {
        // current user is only allowed to get own bookings
        Booking booking = bookingRepo.get(id);
        // null check with 404 response is done by super
        if ((booking != null && !booking.getUserProject().getUser().getId().equals(currentUser.getId()))
            && // neither admin nor manager
            (!currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getRole().equals(UserRole.MANAGER))) {
            log.debug("Rejected deletion of " + booking + " requested by " + currentUser + " - not owner of booking.");
            return Response.status(Status.FORBIDDEN).build();
        }
        return super.get(id);
    }

    @Override
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response update(@PathParam("id") Long id, BookingRepresentation representation) {
        // current user is only allowed to update own bookings - or admin
        Booking booking = bookingRepo.get(id);
        // null check with 404 response is done by super
        if (booking != null && !booking.getUserProject().getUser().getId().equals(currentUser.getId())
            && !currentUser.getRole().equals(UserRole.ADMIN)) {
            log.debug("Rejected update of " + booking + " requested by " + currentUser + " - not owner of booking.");
            return Response.status(Status.FORBIDDEN).build();
        }
        return super.update(id, representation);
    }

    @Override
    public boolean permit(User user, String url, String method) {
        boolean result = super.permit(user, url, method);
        switch (method) {
        case HttpMethod.POST:
            // will be checked by overwritten create
            result = true;
            break;
        case HttpMethod.DELETE:
            // will be checked by overrode delete
            result = true;
            break;
        case HttpMethod.GET:
            // check what kind of get
            // get all
            if (url.contains(ALL_SUFFIX)) {
                // all bookings are only visible by managers and admins
                if (user.getRole().equals(UserRole.MANAGER) || user.getRole().equals(UserRole.ADMIN)) {
                    result = true;
                } else {
                    result = false;
                }
            }
            // get by user
            else if (url.contains("user")) {
                // delegate to getBookingsByUser
                result = true;
            }
            // get by project
            else if (url.contains("project")) {
                // delegate to getBookingsByProject
                result = true;
            }
            // get by id - last alternative
            else {
                // delegate to overrode get
                result = true;
            }
            break;
        case HttpMethod.PUT:
            // delegate to overrode put
            result = true;
            break;
        default:
            break;
        }

        return result;
    }

}
