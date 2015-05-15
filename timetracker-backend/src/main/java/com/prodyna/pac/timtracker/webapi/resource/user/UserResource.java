package com.prodyna.pac.timtracker.webapi.resource.user;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.webapi.RepositoryResource;

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

}