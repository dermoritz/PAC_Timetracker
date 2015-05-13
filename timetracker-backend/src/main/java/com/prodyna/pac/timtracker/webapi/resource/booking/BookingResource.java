package com.prodyna.pac.timtracker.webapi.resource.booking;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.webapi.RepositoryResource;
import com.prodyna.pac.timtracker.webapi.interceptor.Linkable;

@Path("/booking")
@RequestScoped
@Linkable
public class BookingResource extends RepositoryResource<Booking, BookingRepresentation> {

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
    public BookingResource() {
        super(BookingResource.class, Booking.class, BookingRepresentation.class);
    }

    @Override
    public String getResourceMediaType() {
        return USER_XML_MEDIA_TYPE;
    }

    @Override
    protected String[] getMediaTypes() {
        return new String[] {USER_XML_MEDIA_TYPE, USER_JSON_MEDIA_TYPE};
    }

}
