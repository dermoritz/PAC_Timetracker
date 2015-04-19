package com.prodyna.pac.timtracker.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * This table holds which users are registered to which projects. A user can
 * only book times for project he is working on / registered to. Each
 * user/project combination is unique.
 *
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"user","project"}))
public class UserProjects implements Serializable {
    
    /**
     * default id.
     */
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "project")
    private Project project;
    
    /**
     * required.
     */
    public UserProjects() {

    }

    public UserProjects(final User user, final Project project){
        this.user = user;
        this.project = project;
        
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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserProjects other = (UserProjects) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
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
