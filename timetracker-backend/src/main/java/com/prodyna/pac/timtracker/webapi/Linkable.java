package com.prodyna.pac.timtracker.webapi;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;

import com.prodyna.pac.timtracker.persistence.Identifiable;

/**
 * Specifies and implements methods to render a resource self referencable.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public abstract class Linkable implements Identifiable {
    
    /**
     * Needed to create link - base uri.
     */
    private UriInfo uriInfo;
    
    /**
     * Id - also needed in self reference.
     */
    @NotNull
    private Long id;

    /**
     * List of links.
     */
    private String self;
    
    /**
     * 
     * @param uriInfo comes via resource' context.
     */
    public Linkable(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    /**
     * 
     * @return jax-rs resource class to be used to create self reference link.
     */
    protected abstract Class<?> getResourceClass();

    @Override
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
        // add self link
        if (id != null) {
            addSelf(getResourceClass());
        }
    }

    public void setSelf(String selfRef) {
        this.self = selfRef;
    }

    public String getSelf() {
        return self;
    }

    /**
     * Could be used to get a self referencing link.
     * 
     * @param resourceClass
     *            resource class, used to build the link path
     */
    private void addSelf(Class<?> resourceClass) {
        if (uriInfo != null) {
            setSelf(uriInfo.getBaseUriBuilder().clone().path(resourceClass).segment("{id}").build(getId()).toString());
        }
    }

}
