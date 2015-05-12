package com.prodyna.pac.timtracker.webapi.resource;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.webapi.LinkableRepresentation;
import com.prodyna.pac.timtracker.webapi.ResourceLink;

/**
 * Users data representation for rest side.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@XmlRootElement(name = "user", namespace = "urn:ced:user")
public class UserRepresentation extends LinkableRepresentation<User> implements Identifiable {

    @NotNull
    private Long handle;
    @NotNull
    private String name;
    @NotNull
    private String role;

    public UserRepresentation() {
        this(null);
    }

    public UserRepresentation(UriInfo uriInfo) {
        super(User.class, "user", uriInfo);
    }

    @Override
    @XmlTransient
    public Long getId() {
        return handle;
    }

    @XmlElement
    public Long getHandle() {
        return handle;
    }
    //TODO probably better to be removed
    public void setHandle(Long handle) {
        this.handle = handle;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public List<ResourceLink> getLinks() {
        List<ResourceLink> links = super.getLinks();
        if (getUriInfo() != null) {
            if (doesNotContainRel("self") && handle != null) {
                links.add(
                     new ResourceLink(
                                      "self",
                                      getUriInfo().getBaseUriBuilder().clone()
                                                  .path(UserResource.class)
                                                  .segment("{id}")
                                                  .build(handle),
                                      UserResource.USER_XML_MEDIA_TYPE));
            }
        }
        return links;
    }
}
