package com.prodyna.pac.timtracker.webapi.resource.users_projects;

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

import com.prodyna.pac.timtracker.model.UsersProjects;
import com.prodyna.pac.timtracker.model.UsersProjectsRepository;
import com.prodyna.pac.timtracker.webapi.RepositoryResource;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentation;

/**
 * Implements rest crud for {@link UsersProjects} - relies on {@link RepositoryResource}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Path("/usersprojects")
@RequestScoped
public class UsersProjectsResource extends RepositoryResource<UsersProjects, UsersProjectsRepresentation> {
    
    @Inject
    private UsersProjectsRepository upRepository;
    
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
    
    @GET
    @Path("/user/{user_id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUsersProjectsByUser(@PathParam("user_id") Long userId) {
        if (userId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        Collection<UsersProjectsRepresentation> ups = getConverter().from(getUriInfo(), upRepository.getByUserId(userId));
        return Response.ok(new GenericEntity<Collection<UsersProjectsRepresentation>>(ups) {/**/
        }).type(getMediaType()).build();
    }
    
    @GET
    @Path("/project/{project_id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUsersProjectsByProject(@PathParam("project_id") Long projectId) {
        if (projectId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        Collection<UsersProjectsRepresentation> ups = getConverter().from(getUriInfo(), upRepository.getByProjectId(projectId));
        return Response.ok(new GenericEntity<Collection<UsersProjectsRepresentation>>(ups) {/**/
        }).type(getMediaType()).build();
    }
    
}