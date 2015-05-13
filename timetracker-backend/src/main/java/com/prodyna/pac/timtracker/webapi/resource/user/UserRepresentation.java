package com.prodyna.pac.timtracker.webapi.resource.user;

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
@XmlRootElement(name = "user", namespace = "urn:timetracker:user")
public class UserRepresentation extends LinkableRepresentation<User> implements Identifiable {

    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String role;
    
    /**
     * Default.
     */
    public UserRepresentation() {
    }
    
    /**
     * Constructor for {@link LinkableRepresentation}.
     * @param uriInfo
     */
    public UserRepresentation(UriInfo uriInfo) {
        super(User.class, "user", uriInfo);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
            if (doesNotContainRel("self") && id != null) {
                links.add(
                     new ResourceLink(
                                      "self",
                                      getUriInfo().getBaseUriBuilder().clone()
                                                  .path(UserResource.class)
                                                  .segment("{id}")
                                                  .build(id),
                                      UserResource.USER_XML_MEDIA_TYPE));
            }
        }
        return links;
    }
}
