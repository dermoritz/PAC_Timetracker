package com.prodyna.pac.timtracker.webapi.resource.project;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.webapi.Linkable;
@XmlRootElement(name = "project", namespace = "urn:timetracker:project")
public class ProjectRepresentation extends Linkable {
    
    @NotNull
    private String name;

    private String description;
    
    public ProjectRepresentation() {
        this(null);
    }
    
    public ProjectRepresentation(UriInfo uriInfo) {
        super(uriInfo);
    }
    
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
    
    
    
}
