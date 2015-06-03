package com.prodyna.pac.timtracker.webapi.resource.user;

import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
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
import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRepository;
import com.prodyna.pac.timtracker.model.UserRole;
import com.prodyna.pac.timtracker.webapi.RepositoryResource;
import com.prodyna.pac.timtracker.webapi.RepresentationConverter;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentation;

/**
 * Implements rest crud for {@link User} - relies on {@link RepositoryResource}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Path("/user")
@RequestScoped
public class UserResource extends RepositoryResource<User, UserRepresentation> {

    /**
     * Used to fetch all bookings for project.
     */
    @Inject
    private BookingRepository bookingRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    @CurrentUser
    private User currentUser;

    @Inject
    private Logger log;

    /**
     * Converts {@link Booking}<-> {@link BookingRepresentation} - used to get
     * bookings for project.
     */
    @Inject
    private RepresentationConverter<BookingRepresentation, Booking> bookingConverter;

    /**
     * User specific XML {@link MediaType}.
     */
    public static final String MEDIA_SUBTYPE = "; type=user";

    /**
     * Creates {@link RepositoryResource} typed for user.
     */
    public UserResource() {
        super(UserResource.class, User.class, UserRepresentation.class);
    }

    /**
     * 
     * @param userId
     *            id of user
     * @return list of all bookings for given user id
     */
    @GET
    @Path("/{user_id}/bookings")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getBookings(@PathParam("user_id") Long userId) {
        if (userId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        // check if own id or manager or admin
        if (!ownManagerOrAdmin(userId)) {
            log.debug("Rejected request by " + currentUser + " to get bookings for user by id " + userId
                      + " (not own id, neither manager nor admin.)");
            return Response.status(Status.FORBIDDEN).build();
        }
        Collection<BookingRepresentation> bookings = bookingConverter.from(getUriInfo(),
                                                                           bookingRepository.getBookingsByUserId(userId));
        return Response.ok(new GenericEntity<Collection<BookingRepresentation>>(bookings) {/**/
        }).type(getResourceMediaType()).build();
    }

    /**
     * fetches user by name (case insensitive).
     * 
     * @param userName
     *            name of user
     * @return user with given name or {@link Status#NOT_FOUND}
     */
    @GET
    @Path("/name/{user_name}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserByName(@PathParam("user_name") String userName) {
        // check if own name or manager or admin
        if (!ownManagerOrAdmin(userName)) {
            log.debug("Rejected request by " + currentUser + " to get user by name " + userName
                      + " (not own name, neither manager nor admin.)");
            return Response.status(Status.FORBIDDEN).build();
        }
        User user = userRepository.getByName(userName);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(getConverter().from(getUriInfo(), user))
                       .type(getResourceMediaType())
                       .lastModified(user.getLastModified())
                       .build();
    }

    @Override
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response get(@PathParam("id") Long id) {
        if (!ownManagerOrAdmin(id)) {
            log.debug("Rejected request by " + currentUser + " to get user by id " + id
                      + " (not own id, neither manager nor admin.)");
            return Response.status(Status.FORBIDDEN).build();
        }
        return super.get(id);
    }

    /**
     * 
     * @return current user - the user that is logged in.
     */
    @GET
    @Path("/current")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCurrent() {
        return Response.ok(getConverter().from(getUriInfo(), currentUser))
                       .type(getResourceMediaType())
                       .lastModified(currentUser.getLastModified())
                       .build();
    }

    @Override
    public boolean permit(User user, String url, String method) {
        boolean result = super.permit(user, url, method);
        switch (method) {
        case HttpMethod.POST:
            // admins or managers are permitted to create users
            if (user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.MANAGER)) {
                result = true;
            } else {
                result = false;
            }
            break;
        case HttpMethod.DELETE:
            // admins or managers are permitted to delete users
            if (user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.MANAGER)) {
                result = true;
            } else {
                result = false;
            }
            break;
        case HttpMethod.GET:
            // check what kind of get
            // current user
            if (url.contains("current")) {
                // all are allowed to get current user (own user)
                result = true;
            } else if (url.contains("bookings")) {
                // delegate to method
                result = true;
            } else if (url.contains("name")) {
                // delegate to method
                result = true;
            }
            // get by id
            else {
                // delegate to overrode get
                result = true;
            }
            break;
        case HttpMethod.PUT:
            // admins or managers are permitted to update users
            if (user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.MANAGER)) {
                result = true;
            } else {
                result = false;
            }
            break;
        default:
            break;
        }
        return result;
    }

    @Override
    protected String getMediaSupType() {
        return MEDIA_SUBTYPE;
    }

}