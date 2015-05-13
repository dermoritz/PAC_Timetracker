package com.prodyna.pac.timtracker.model;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Preconditions;
import com.prodyna.pac.timtracker.persistence.BaseEntity;

/**
 * Stores registered projects per user.
 * 
 * @author moritz
 *
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user", "project"}))
public class UsersProjects extends BaseEntity {
    /**
     * default id.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * user id.
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user")
    private User user;
    
    /**
     * project id.
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "project")
    private Project project;

    /**
     * required as this is used as {@link EmbeddedId}.
     */
    UsersProjects() {

    }

    /**
     * 
     * @param user
     *            the user to be linked to a project
     * @param project
     *            the linked project
     */
    public UsersProjects(final User user, final Project project) {
        this.user = Preconditions.checkNotNull(user, "User must not be null.");
        this.project = Preconditions.checkNotNull(project, "Project must not be null.");

    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
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
    public String toString() {
        return user + "," + project;
    }

}
