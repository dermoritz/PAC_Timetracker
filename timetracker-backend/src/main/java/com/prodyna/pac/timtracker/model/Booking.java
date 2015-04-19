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
     * A booking must have a project related.
     */
    @ManyToOne
    @JoinColumn(name = "project_id")
    @NotNull
    private Project project;

    /**
     * A booking must have an owner.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User owner;

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
     * @return true if start is befor end. false otherwise (if equal or after end).
     */
    @AssertTrue(message = "Start must before end.")
    public final boolean isStartBeforeEnd() {
        return start.compareTo(end) < 0;
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
        return project;
    }

    /**
     * @param project
     *            the project to set
     */
    public final void setProject(final Project project) {
        this.project = project;
    }

    /**
     * @return the owner
     */
    public final User getOwner() {
        return owner;
    }

    /**
     * @param owner
     *            the owner to set
     */
    public final void setOwner(final User owner) {
        this.owner = owner;
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

    /**
     * Uses start, end id, owner and project.
     * 
     * @return hashCode
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + version;
        return result;
    }

    /**
     * @param obj
     *            the other object
     * @return true if start, end id, owner and project are equal.
     */
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
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        if (project == null) {
            if (other.project != null) {
                return false;
            }
        } else if (!project.equals(other.project)) {
            return false;
        }
        if (start == null) {
            if (other.start != null) {
                return false;
            }
        } else if (!start.equals(other.start)) {
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
        result += "owner: " + owner != null ? owner.getName() : null + ",";
        result += "project: " + project != null ? project.getName() : null + ",";
        return result;
    }

}