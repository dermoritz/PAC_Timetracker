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
    
    @Override
    public String toString() {
        return "UserRepresentation [getName()=" + getName() + ", getRole()=" + getRole() + ", getId()=" + getId()
               + ", getSelf()=" + getSelf() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof UserRepresentation)) {
            return false;
        }
        UserRepresentation other = (UserRepresentation) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (role == null) {
            if (other.role != null) {
                return false;
            }
        } else if (!role.equals(other.role)) {
            return false;
        }
        return true;
    }
}
