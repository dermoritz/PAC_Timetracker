package com.prodyna.pac.timtracker.webapi;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.prodyna.pac.timtracker.model.UsersProjects;

import com.prodyna.pac.timtracker.model.Booking;

/**
 * Models links between entities for the rest side (e.g. a {@link Booking} has a
 * {@link UsersProjects}).
 * Links source type to series of links.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 * @param <T>
 */
public abstract class LinkableRepresentation<T> implements Representation<T> {

    private List<ResourceLink> links;
    private Class<T> sourceType;
    private String representationType;
    private UriInfo uriInfo;

    protected LinkableRepresentation() {
    }

    public LinkableRepresentation(Class<T> sourceType, String representationType, UriInfo uriInfo) {
        this.sourceType = sourceType;
        this.representationType = representationType;
        this.uriInfo = uriInfo;
    }

    @XmlElement(name = "links", namespace = "urn:timetracker:links")
    public List<ResourceLink> getLinks() {
        if (this.links == null) {
            this.links = new ArrayList<ResourceLink>();
        }
        return links;
    }

    public void addLink(ResourceLink link) {
        getLinks().add(link);
    }

    public boolean doesNotContainRel(String rel) {
        return !containRel(rel);
    }

    public boolean containRel(String rel) {
        if (links == null || links.size() == 0) {
            return false;
        }
        for (ResourceLink link : links) {
            if (rel.equals(link.getRel())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @XmlTransient
    public Class<T> getSourceType() {
        return sourceType;
    }

    @Override
    @XmlTransient
    public String getRepresentationType() {
        return representationType;
    }

    @XmlTransient
    public UriInfo getUriInfo() {
        return uriInfo;
    }
}
