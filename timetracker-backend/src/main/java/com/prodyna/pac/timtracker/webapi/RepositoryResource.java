package com.prodyna.pac.timtracker.webapi;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;

import com.prodyna.pac.timtracker.cdi.CurrentUser;
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRole;
import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.persistence.Repository;
import com.prodyna.pac.timtracker.persistence.Timestampable;
import com.prodyna.pac.timtracker.webapi.security.SecureResource;

/**
 * Abstraction for model entities exposed via rest as resources.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 * 
 * @param <DOMAIN>
 *            type of domain object (entity)
 * 
 * @param <REP>
 *            type of representation class (rest)
 *
 *
 */
public abstract class RepositoryResource<DOMAIN extends Identifiable & Timestampable, REP extends Identifiable>
                                                                                                                implements
                                                                                                                Resource,
                                                                                                                SecureResource {
    /**
     * Path to request all.
     */
    public static final String ALL_SUFFIX = "/all";

    /**
     * Used for paginated fetch - page size parameter.
     */
    public static final String QUERY_PARAM_PAGE_SIZE = "pageSize";

    /**
     * Used for paginated fetch - page size parameter.
     */
    public static final String QUERY_PARAM_PAGE = "page";

    /**
     * Resource type.
     */
    private Class<? extends Resource> resourceClass;

    /**
     * Domain type.
     */
    private Class<DOMAIN> domainClass;

    /**
     * Representation type.
     */
    private Class<REP> representationClass;

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders headers;

    @Inject
    private Repository<DOMAIN> repository;

    @Inject
    private RepresentationConverter<REP, DOMAIN> converter;

    @Inject
    private Logger log;

    @Inject
    @CurrentUser
    private User currentUser;

    /**
     * Needed by CDI
     */
    protected RepositoryResource() {

    }

    /**
     * 
     * @param resourceClass
     * @param domainClass
     * @param representationClass
     */
    public RepositoryResource(Class<? extends Resource> resourceClass, Class<DOMAIN> domainClass,
                              Class<REP> representationClass) {
        this.resourceClass = resourceClass;
        this.domainClass = domainClass;
        this.representationClass = representationClass;
    }

    @Override
    public Class<? extends Resource> getResourceClass() {
        return resourceClass;
    }

    public Class<DOMAIN> getDomainClass() {
        return domainClass;
    }

    public Class<REP> getRepresentationClass() {
        return representationClass;
    }

    protected Repository<DOMAIN> getRepository() {
        return repository;
    }

    protected RepresentationConverter<REP, DOMAIN> getConverter() {
        return converter;
    }

    protected UriInfo getUriInfo() {
        return uriInfo;
    }

    /**
     * Creates and persists the given object.
     * 
     * @param representation
     * @return
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(REP representation) {
        DOMAIN entity = getConverter().to(uriInfo, representation);

        DOMAIN storedEntity = getRepository().store(entity);
        return Response.created(UriBuilder.fromResource(getResourceClass()).segment("{id}").build(storedEntity.getId()))
                       .build();
    }

    /**
     * Deletes the requested object.
     * 
     * @param id
     *            identifies the object
     * @return either nothing on success or 404 if entity was not found.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        DOMAIN entity = getRepository().get(id);
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        getRepository().remove(entity);
        return Response.noContent().build();
    }

    /**
     * Retrieves the requested object by id.
     * 
     * @param id
     *            id of requested object
     * @return requested object or 404 if not found
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response get(@PathParam("id") Long id) {
        DOMAIN entity = getRepository().get(id);
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).type(getResourceMediaType()).build();
        }

        return Response.ok(getConverter().from(uriInfo, entity))
                       .type(getResourceMediaType())
                       .lastModified(entity.getLastModified())
                       .build();
    }

    /**
     * Retrieves all entities. If parameters are set (both > 0) output is
     * paginated.
     * 
     * @param page
     *            number of current page for paginated output
     * @param pageSize
     *            entities per page for paginated output
     * @return list with all entities
     */
    @GET
    @Path(ALL_SUFFIX)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAll(@QueryParam(QUERY_PARAM_PAGE) Integer page,
                           @QueryParam(QUERY_PARAM_PAGE_SIZE) Integer pageSize) {
        // only if both parameters are given use paginated output
        Collection<REP> results;
        if (page != null && pageSize != null && page > 0 && pageSize > 0) {
            results = getConverter().from(uriInfo, getRepository().getAllPaginated(page, pageSize));
        } else {
            results = getConverter().from(uriInfo, getRepository().getAll());
        }
        return Response.ok(results).type(getResourceMediaType()).build();
    }

    /**
     * Updates the given object.
     * 
     * @param id
     *            id of object to be updated
     * @param representation
     *            type of object
     * @return empty response on success or 404 if object was not found
     */
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response update(@PathParam("id") Long id, REP representation) {
        DOMAIN entity = getRepository().get(id);
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        DOMAIN updatedEntity = getConverter().update(uriInfo, representation, entity);
        DOMAIN storedEntity = getRepository().store(updatedEntity);

        return Response.noContent().links(getLink(storedEntity.getId())).build();
    }

    // Internal Helpers

    /**
     * 
     * @return array of accepted/sent media types
     */
    protected String[] getMediaTypes() {
        return new String[] {MediaType.APPLICATION_JSON + getMediaSupType(),
                             MediaType.APPLICATION_XML + getMediaSupType()};
    }

    /**
     * 
     * @return last part for media type e.g. "; type=booking"
     */
    protected abstract String getMediaSupType();

    /**
     * Tries to match media type of request with available (by Resource) types.
     * If no match is found the first type is returned.
     * 
     * @return media type
     */
    @Override
    public String getResourceMediaType() {
        // get types from child class
        String[] mediaTypes = getMediaTypes();
        if (mediaTypes.length < 1) {
            throw new IllegalStateException("Resource class "
                                            + resourceClass
                                            + " does not correctly implement getMediaTypes."
                                            + " It should return at least one type.");
        }
        String result = mediaTypes[0];
        MediaType requestType = headers.getMediaType();
        if (requestType != null) {
            for (String string : mediaTypes) {
                if (requestType.isCompatible(MediaType.valueOf(string))) {
                    result = string;
                }
            }
        }
        return result;
    }

    /**
     * {@link UserRole#ADMIN} is allowed to do all. False is returned for all
     * other roles - should be overwritten if needed.
     */
    @Override
    public boolean permit(User user, String url, String method) {
        // admin is allowed to do all
        if (user.getRole().equals(UserRole.ADMIN)) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @param userId
     *            user id used in request.
     * @return true if current user is manager OR admin OR given id is it's own
     *         id.
     */
    public boolean ownManagerOrAdmin(Long userId) {
        return currentUser.getId().equals(userId) || currentUser.getRole().equals(UserRole.MANAGER)
               || currentUser.getRole().equals(UserRole.ADMIN);
    }

    public boolean ownManagerOrAdmin(String userName) {
        return currentUser.getName().equals(userName) || currentUser.getRole().equals(UserRole.MANAGER)
               || currentUser.getRole().equals(UserRole.ADMIN);
    }

    /**
     * Link to get resource of given id.
     * 
     * @param id
     *            id of resource
     * @return Link to be used in header.
     */
    protected Link getLink(Long id) {
        return Link.fromUri(getUriInfo().getBaseUriBuilder()
                                        .path(getResourceClass())
                                        .path(getResourceClass(), "get")
                                        .build(id)).rel("get").type(getResourceMediaType()).build();
    }

    /**
     * Link to update resource of given id.
     * 
     * @param id
     *            id of resource
     * @return Link to be used in header.
     */
    protected Link putLink(Long id) {
        return Link.fromUri(getUriInfo().getBaseUriBuilder()
                                        .path(getResourceClass())
                                        .path(getResourceClass(), "update")
                                        .build(id)).rel("put").type(getResourceMediaType()).build();
    }

    /**
     * Link to delete resource of given id.
     * 
     * @param id
     *            id of resource
     * @return Link to be used in header.
     */
    protected Link deleteLink(Long id) {
        return Link.fromUri(getUriInfo().getBaseUriBuilder()
                                        .path(getResourceClass())
                                        .path(getResourceClass(), "delete")
                                        .build(id)).rel("delete").type(getResourceMediaType()).build();
    }
    
    /**
     * Link to delete resource of given id.
     * 
     * @param id
     *            id of resource
     * @return Link to be used in header.
     */
    protected Link createLink() {
        return Link.fromUri(getUriInfo().getBaseUriBuilder()
                                        .path(getResourceClass())
                                        .path(getResourceClass(), "create")
                                        .build()).rel("post").type(getResourceMediaType()).build();
    }
    
}
