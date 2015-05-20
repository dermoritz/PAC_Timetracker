package com.prodyna.pac.timtracker.webapi.resource.users_projects;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import com.prodyna.pac.timtracker.model.UsersProjects;
import com.prodyna.pac.timtracker.webapi.Linkable;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentation;

/**
 * DTO for {@link UsersProjects}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@XmlRootElement(name = "UsersProjects", namespace = "urn:timetracker:usersProjects")
public class UsersProjectsRepresentation extends Linkable {

    @NotNull
    private UserRepresentation user;

    @NotNull
    private ProjectRepresentation project;

    public UsersProjectsRepresentation() {
        this(null);
    }

    public UsersProjectsRepresentation(UriInfo uriInfo) {
        super(uriInfo);
    }

    /**
     * @return the user
     */
    public UserRepresentation getUser() {
        return user;
    }

    /**
     * @param user
     *            the user to set
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
     * @param project
     *            the project to set
     */
    public void setProject(ProjectRepresentation project) {
        this.project = project;
    }

    @Override
    protected Class<?> getResourceClass() {
        return UsersProjectsResource.class; 
    }

    @Override
    public String toString() {
        return "UsersProjectsRepresentation [getUser()=" + getUser() + ", getProject()=" + getProject() + ", getId()="
               + getId() + ", getSelf()=" + getSelf() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        if (!(obj instanceof UsersProjectsRepresentation)) {
            return false;
        }
        UsersProjectsRepresentation other = (UsersProjectsRepresentation) obj;
        if (project == null) {
            if (other.project != null) {
                return false;
            }
        } else if (!project.equals(other.project)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }
        return true;
    }

}
