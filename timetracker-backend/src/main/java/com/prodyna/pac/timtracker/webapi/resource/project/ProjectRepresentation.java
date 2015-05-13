package com.prodyna.pac.timtracker.webapi.resource.project;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.webapi.LinkableRepresentation;
@XmlRootElement(name = "project", namespace = "urn:timetracker:project")
public class ProjectRepresentation extends LinkableRepresentation<Project> implements Identifiable {
    
    @NotNull
    private String name;
    
    @NotNull
    private Long id;
    
    private String description;
    
    /**
     * Default.
     */
    public ProjectRepresentation() {
        
    }
    
    /**
     * Used for {@link LinkableRepresentation}.
     * @param uriInfo
     */
    public ProjectRepresentation(UriInfo uriInfo) {
        super(Project.class, "project", uriInfo);
    }
    
    @Override
    public Long getId() {
        return id;
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

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
}
