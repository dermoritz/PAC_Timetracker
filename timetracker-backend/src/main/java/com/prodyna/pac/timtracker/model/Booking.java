package com.prodyna.pac.timtracker.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * A booking has disjoint start and end time an owner ( {@link User} ) and a
 * {@link Project}.
 * 
 * @author moritz
 *
 */
@Entity
public class Booking implements Serializable {

    /**
     * Validation message if end is before start.
     */
    private static final String START_MUST_BEFORE_END = "Start must before end.";
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * internal id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    /**
     * Used for optimistic locking.
     */
    @Version
    @Column(name = "version")
    private int version;

    /**
     * Link to user and project.
     */
    @Column
    @NotNull
    private UserProjects userProject;

    /**
     * A booking always has a start time.
     */
    @Column
    @NotNull
    private Timestamp start;

    /**
     * A booking always has an end time.
     */
    @Column
    @NotNull
    private Timestamp end;

    /**
     * 
     * @return true if start is befor end. false otherwise (if equal or after
     *         end).
     */
    @AssertTrue(message = START_MUST_BEFORE_END)
    public final boolean isStartBeforeEnd() {
        return start.compareTo(end) < 0;
    }

    /**
     * @return the userProject
     */
    public final UserProjects getUserProject() {
        return userProject;
    }

    /**
     * @param userProject
     *            the userProject to set
     */
    public final void setUserProject(final UserProjects userProject) {
        this.userProject = userProject;
    }

    /**
     * @return the id
     */
    public final Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public final void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public final int getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public final void setVersion(final int version) {
        this.version = version;
    }

    /**
     * @return the project
     */
    public final Project getProject() {
        return userProject == null ? null : userProject.getProject();
    }

    /**
     * @return the owner
     */
    public final User getOwner() {
        return userProject == null ? null : userProject.getUser();
    }

    /**
     * @return the start
     */
    public final Timestamp getStart() {
        return start;
    }

    /**
     * @param start
     *            the start to set
     */
    public final void setStart(final Timestamp start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public final Timestamp getEnd() {
        return end;
    }

    /**
     * @param end
     *            the end to set
     */
    public final void setEnd(final Timestamp end) {
        this.end = end;
    }

   

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((userProject == null) ? 0 : userProject.hashCode());
        result = prime * result + version;
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
        Booking other = (Booking) obj;
        if (end == null) {
            if (other.end != null) {
                return false;
            }
        } else if (!end.equals(other.end)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (start == null) {
            if (other.start != null) {
                return false;
            }
        } else if (!start.equals(other.start)) {
            return false;
        }
        if (userProject == null) {
            if (other.userProject != null) {
                return false;
            }
        } else if (!userProject.equals(other.userProject)) {
            return false;
        }
        if (version != other.version) {
            return false;
        }
        return true;
    }

    @Override
    public final String toString() {
        String result = getClass().getSimpleName() + ": ";
        result += "start: " + start + ", ";
        result += "end: " + end + ", ";
        result += "owner: " + userProject != null ? userProject.getUser().getName() : null + ",";
        result += "project: " + userProject != null ? userProject.getProject().getName() : null + ",";
        return result;
    }

}