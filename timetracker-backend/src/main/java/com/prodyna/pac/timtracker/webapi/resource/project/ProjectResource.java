package com.prodyna.pac.timtracker.webapi.resource.project;

import java.net.URI;
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
import javax.ws.rs.core.UriInfo;

import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.BookingRepository;
import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.model.ProjectRepository;
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRole;
import com.prodyna.pac.timtracker.webapi.RepositoryResource;
import com.prodyna.pac.timtracker.webapi.RepresentationConverter;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentation;

/**
 * Crud rest resource for {@link Project}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Path("/project")
@RequestScoped
public class ProjectResource extends RepositoryResource<Project, ProjectRepresentation> {

    /**
     * Used to fetch all bookings for project.
     */
    @Inject
    private BookingRepository bookingRepository;

    /**
     * Converts {@link Booking}<-> {@link BookingRepresentation} - used to get
     * bookings for project.
     */
    @Inject
    private RepresentationConverter<BookingRepresentation, Booking> bookingConverter;

    /**
     * Used to query project specific stuff.
     */
    @Inject
    private ProjectRepository projectRepository;

    /**
     * User specific XML {@link MediaType}.
     */
    public static final String PROJECT_XML_MEDIA_TYPE = MediaType.APPLICATION_XML + "; type=project";

    /**
     * User specific JSON {@link MediaType}.
     */
    public static final String PROJECT_JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON + "; type=project";

    /**
     * Creates {@link RepositoryResource} typed for user.
     */
    public ProjectResource() {
        super(ProjectResource.class, Project.class, ProjectRepresentation.class);
    }

    @Override
    public String getResourceMediaType() {
        return PROJECT_XML_MEDIA_TYPE;
    }

    @Override
    protected String[] getMediaTypes() {
        return new String[] {PROJECT_XML_MEDIA_TYPE, PROJECT_JSON_MEDIA_TYPE};
    }

    /**
     * 
     * @param projectId
     *            id of project
     * @return list of all bookings for given project id
     */
    @GET
    @Path("/{project_id}/bookings")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getBookings(@PathParam("project_id") Long projectId) {
        if (projectId == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        Collection<BookingRepresentation> bookings = bookingConverter.from(getUriInfo(),
                                                                           bookingRepository.getBookingsByProjectId(projectId));
        return Response.ok(new GenericEntity<Collection<BookingRepresentation>>(bookings) {/**/
        }).type(getMediaType()).build();
    }

    /**
     * fetches project by name (case insensitive).
     * 
     * @param projectName
     *            name of project
     * @return project with given name or {@link Status#NOT_FOUND}
     */
    @GET
    @Path("/name/{project_name}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getProjectByName(@PathParam("project_name") String projectName) {
        Project project = projectRepository.getByName(projectName);
        if (project == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(getConverter().from(getUriInfo(), project))
                       .type(getMediaType())
                       .lastModified(project.getLastModified())
                       .build();
    }

    @Override
    public boolean permit(User user, String url, String method) {
        boolean result = super.permit(user, url, method);
        switch (method) {
        case HttpMethod.POST:
            // admins or managers are permitted to create projects
            if (user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.MANAGER)) {
                result = true;
            } else {
                result = false;
            }
            break;
        case HttpMethod.DELETE:
            // admins or managers are permitted to delete projects
            if (user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.MANAGER)) {
                result = true;
            } else {
                result = false;
            }
            break;
        case HttpMethod.GET:
            // check what kind of get
            // get all bookings for project
            if (url.contains("bookings")) {
                // all bookings are only visible by managers and admins
                if (user.getRole().equals(UserRole.MANAGER) || user.getRole().equals(UserRole.ADMIN)) {
                    result = true;
                } else {
                    result = false;
                }
            }
            // get by name and by id is permitted to all
            else {
                // delegate to overrode get
                result = true;
            }
            break;
        case HttpMethod.PUT:
            // admins or managers are permitted to update projects
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
