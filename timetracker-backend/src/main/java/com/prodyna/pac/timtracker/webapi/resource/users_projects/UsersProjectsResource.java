package com.prodyna.pac.timtracker.webapi.resource.users_projects;

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
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRole;
import com.prodyna.pac.timtracker.model.UsersProjects;
import com.prodyna.pac.timtracker.model.UsersProjectsRepository;
import com.prodyna.pac.timtracker.webapi.RepositoryResource;

/**
 * Implements rest crud for {@link UsersProjects} - relies on
 * {@link RepositoryResource}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Path("/usersprojects")
@RequestScoped
public class UsersProjectsResource extends RepositoryResource<UsersProjects, UsersProjectsRepresentation> {

    @Inject
    private UsersProjectsRepository upRepository;
    
    @Inject
    @CurrentUser
    private User currentUser;
    
    @Inject
    private Logger log;
    
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
        if(!ownManagerOrAdmin(userId)){
            log.debug("Rejected request by " + currentUser + " to get registered projects for user id " + userId
                      + " (not own id, neither manager nor admin.)");
            return Response.status(Status.FORBIDDEN).build();
        }
        Collection<UsersProjectsRepresentation> ups = getConverter().from(getUriInfo(),
                                                                          upRepository.getByUserId(userId));
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
        Collection<UsersProjectsRepresentation> ups = getConverter().from(getUriInfo(),
                                                                          upRepository.getByProjectId(projectId));
        return Response.ok(new GenericEntity<Collection<UsersProjectsRepresentation>>(ups) {/**/
        }).type(getMediaType()).build();
    }

    @Override
    public boolean permit(User user, String url, String method) {
        boolean result = super.permit(user, url, method);
        switch (method) {
        case HttpMethod.POST:
            // admins or managers are permitted to create usersprojects
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
            if (url.contains("user")) {
                // delegate to method
                result = true;
            } else if (url.contains("project")) {
                // all user registered for given project - only visible by admin/manager
                if (user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.MANAGER)) {
                    result = true;
                } else {
                    result = false;
                }
            } 
            // get by id
            else {
                //readable by all
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
}