package com.prodyna.pac.timtracker.webapi.resource.users_projects;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.prodyna.pac.timtracker.model.UsersProjects;
import com.prodyna.pac.timtracker.webapi.RepositoryResource;

/**
 * Implements rest crud for {@link UsersProjects} - relies on {@link RepositoryResource}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Path("/usersprojects")
@RequestScoped
public class UsersProjectsResource extends RepositoryResource<UsersProjects, UsersProjectsRepresentation> {
    
    /**
     * User specific XML {@link MediaType}. 
     */
    public static final String USERSPROJECTS_XML_MEDIA_TYPE = MediaType.APPLICATION_XML + "; type=usersprojects";
    
    /**
     * User specific JSON {@link MediaType}.
     */
    public static final String USERSPROJECTS_JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON + "; type=usersprojects";
    
    /**
     * Creates {@link RepositoryResource} typed for usersProjects.
     */
    public UsersProjectsResource() {
        super(UsersProjectsResource.class, UsersProjects.class, UsersProjectsRepresentation.class);
    }

    @Override
    public String getResourceMediaType() {
        return USERSPROJECTS_XML_MEDIA_TYPE;
    }

    @Override
    protected String[] getMediaTypes() {
        return new String[] {USERSPROJECTS_XML_MEDIA_TYPE, USERSPROJECTS_JSON_MEDIA_TYPE};
    }

}