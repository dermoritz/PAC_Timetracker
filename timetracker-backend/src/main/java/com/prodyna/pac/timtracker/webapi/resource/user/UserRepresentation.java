package com.prodyna.pac.timtracker.webapi.resource.user;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.webapi.Linkable;

/**
 * Users data representation for rest side.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@XmlRootElement(name = "user", namespace = "urn:timetracker:user")
public class UserRepresentation extends Linkable {

    @NotNull
    private String name;

    @NotNull
    private String role;

    public UserRepresentation() {
        this(null);
    }
    
    public UserRepresentation(UriInfo uriInfo) {
        super(uriInfo);
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
    protected Class<?> getResourceClass() {
        return UserResource.class; 
    }

}
