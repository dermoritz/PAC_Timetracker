package com.prodyna.pac.timtracker.model;

import java.io.Serializable;

/**
 * Key formed by user project combination.
 * @author moritz
 *
 */
public class UserProjectKey implements Serializable {
    /**
     * default id.
     */
    private static final long serialVersionUID = 1L;
    /**
     * user id.
     */
    private Long user;
    /**
     * project id.
     */
    private Long project;
    
    /**
     * required.
     */
    public UserProjectKey() {
        
    }
    
    /**
     * 
     * @param userId id of user
     * @param projectId id of project
     */
    public UserProjectKey(final long userId, final long projectId){
        user = userId;
        project = projectId;
    }
    
    
    
    /**
     * @return the user
     */
    public final Long getUser() {
        return user;
    }

    /**
     * @return the project
     */
    public final Long getProject() {
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
        UserProjectKey other = (UserProjectKey) obj;
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