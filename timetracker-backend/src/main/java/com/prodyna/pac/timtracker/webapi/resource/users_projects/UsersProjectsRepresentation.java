package com.prodyna.pac.timtracker.webapi.resource.users_projects;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.prodyna.pac.timtracker.model.UsersProjects;
import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentation;

/**
 * DTO for {@link UsersProjects}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@XmlRootElement(name = "UsersProjects", namespace = "urn:timetracker:usersProjects")
public class UsersProjectsRepresentation implements Identifiable {
    
    @NotNull
    private UserRepresentation user;
    
    @NotNull
    private ProjectRepresentation project;
    
    @NotNull
    private Long id;

    @Override
    public Long getId() {
        return id;
    }
  
    /**
     * @return the user
     */
    public UserRepresentation getUser() {
        return user;
    }


    /**
     * @param user the user to set
     */
    public void setUser(UserRepresentation user) {
        this.user = user;
    }


    /**
     * @return the project
     */
    public ProjectRepresentation getProject() {
        return project;
    }


    /**
     * @param project the project to set
     */
    public void setProject(ProjectRepresentation project) {
        this.project = project;
    }


    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
}
