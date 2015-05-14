package com.prodyna.pac.timtracker.webapi;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;

import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.persistence.Repository;
import com.prodyna.pac.timtracker.persistence.Timestampable;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentation;

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
public abstract class RepositoryResource<DOMAIN extends Identifiable & Timestampable, REP extends Representation<DOMAIN>>
                                                                                                                          implements
                                                                                                                          Resource {

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
            return Response.status(Status.NOT_FOUND).type(getMediaType()).build();
        }

        return Response.ok(getConverter().from(uriInfo, entity))
                       .type(getMediaType())
                       .lastModified(entity.getLastModified())
                       .build();
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
        getRepository().store(updatedEntity);

        return Response.noContent().build();
    }

    // Internal Helpers

    /**
     * Each extending class will derive it's own media types. The first one is
     * used as default if no other matches from request.
     * 
     * @return array of media types
     */
    protected abstract String[] getMediaTypes();

    /**
     * Tries to match media type of request with available (by Resource) types.
     * If no match is found the first type is returned.
     * 
     * @return
     */
    private String getMediaType() {
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
}
