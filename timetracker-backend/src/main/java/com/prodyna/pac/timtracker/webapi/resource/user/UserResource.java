package com.prodyna.pac.timtracker.webapi.resource.user;

import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.BookingRepository;
import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRepository;
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

    /**
     * Converts {@link Booking}<-> {@link BookingRepresentation} - used to get
     * bookings for project.
     */
    @Inject
    private RepresentationConverter<BookingRepresentation, Booking> bookingConverter;
    
    /**
     * User specific XML {@link MediaType}. 
     */
    public static final String USER_XML_MEDIA_TYPE = MediaType.APPLICATION_XML + "; type=user";
    
    /**
     * User specific JSON {@link MediaType}.
     */
    public static final String USER_JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON + "; type=user";
    
    /**
     * Creates {@link RepositoryResource} typed for user.
     */
    public UserResource() {
        super(UserResource.class, User.class, UserRepresentation.class);
    }

    @Override
    public String getResourceMediaType() {
        return USER_XML_MEDIA_TYPE;
    }

    @Override
    protected String[] getMediaTypes() {
        return new String[] {USER_XML_MEDIA_TYPE, USER_JSON_MEDIA_TYPE};
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
        Collection<BookingRepresentation> bookings = bookingConverter.from(getUriInfo(),
                                                                           bookingRepository.getBookingsByUserId(userId));
        return Response.ok(new GenericEntity<Collection<BookingRepresentation>>(bookings) {/**/
        }).type(getMediaType()).build();
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
    public Response getProjectByName(@PathParam("user_name") String userName) {
        User user = userRepository.getByName(userName);
        if (user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(getConverter().from(getUriInfo(), user))
                       .type(getMediaType())
                       .lastModified(user.getLastModified())
                       .build();
    }
    
}