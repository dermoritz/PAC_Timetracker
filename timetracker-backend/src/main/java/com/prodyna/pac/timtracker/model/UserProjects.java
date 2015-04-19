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

   

}
