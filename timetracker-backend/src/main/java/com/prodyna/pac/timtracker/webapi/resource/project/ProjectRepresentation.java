package com.prodyna.pac.timtracker.webapi.resource.project;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.prodyna.pac.timtracker.webapi.Linkable;
@XmlRootElement(name = "project", namespace = "urn:timetracker:project")
public class ProjectRepresentation extends Linkable {

    @NotNull
    private String name;

    private String description;
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    protected Class<?> getResourceClass() {
        return ProjectResource.class; 
    }

    @Override
    public String toString() {
        return "ProjectRepresentation [getName()=" + getName() + ", getDescription()=" + getDescription()
               + ", getId()=" + getId() + ", getSelf()=" + getSelf() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (!(obj instanceof ProjectRepresentation)) {
            return false;
        }
        ProjectRepresentation other = (ProjectRepresentation) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
}
