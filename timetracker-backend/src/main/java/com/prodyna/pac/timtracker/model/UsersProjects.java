package com.prodyna.pac.timtracker.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;

import com.google.common.base.Preconditions;

/**
 * Key formed by user / project combination.
 * 
 * @author moritz
 *
 */
@Embeddable
public class UsersProjects implements Serializable {
    /**
     * default id.
     */
    private static final long serialVersionUID = 1L;
    /**
     * user id.
     */
    @Column
    private User user;
    /**
     * project id.
     */
    @Column
    private Project project;

    /**
     * required as this is used as {@link EmbeddedId}.
     */
    public UsersProjects() {

    }
    
    /**
     * 
     * @param user the user to be linked to a project
     * @param project the linked project
     */
    public UsersProjects(final User user, final Project project) {
        this.user =  Preconditions.checkNotNull(user, "User must not be null.");
        this.project = Preconditions.checkNotNull(project, "Project must not be null.");
        
    }

    /**
     * @return the user
     */
    public final User getUser() {
        return user;
    }

    /**
     * @return the project
     */
    public final Project getProject() {
        return project;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UsersProjects other = (UsersProjects) obj;
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

    /**
     * @return userId,projectId.
     */
    @Override
    public final String toString() {
        return user + "," + project;
    }

}
