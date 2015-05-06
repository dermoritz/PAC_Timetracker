package com.prodyna.pac.timtracker.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.spi.DirStateFactory.Result;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;
import com.prodyna.pac.timtracker.persistence.BaseEntity;
import com.prodyna.pac.timtracker.persistence.Identifiable;

/**
 * A booking has disjoint start and end time an owner ( {@link User} ) and a
 * {@link Project}.
 * 
 * @author moritz
 *
 */
@Entity
public class Booking extends BaseEntity {

    /**
     * Validation message if end is before start.
     */
    private static final String START_MUST_BEFORE_END = "Start must before end.";
    /**
     * default.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Used for optimistic locking.
     */
    @Version
    @Column(name = "version")
    private int version;

    /**
     * Link to user and project.
     */
    @NotNull
    private UsersProjects userProject;

    /**
     * A booking always has a start time.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date start;

    /**
     * A booking always has an end time.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date end;

    /**
     * Required by JPA.
     */
    Booking() {

    }

    /**
     * 
     * @param userProject
     *            links this to user and project
     * @param start
     *            start time
     * @param end
     *            end time
     */
    public Booking(final UsersProjects userProject, final Date start, final Date end) {
        this.userProject = Preconditions.checkNotNull(userProject, "userProject must not be null.");
        setStart(start);
        setEnd(end);
    }

    /**
     * 
     * @return true if start is befor end. false otherwise (if equal or after
     *         end).
     */
    @AssertTrue(message = START_MUST_BEFORE_END)
    public final boolean isStartBeforeEnd() {
        boolean result = false;
        if (start != null && end != null) {
            result = start.compareTo(end) < 0;
        }
        return result;
    }

    /**
     * @return the userProject
     */
    public final UsersProjects getUserProject() {
        return userProject;
    }

    /**
     * @return the version
     */
    public final int getVersion() {
        return version;
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
    public final Date getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public final Date getEnd() {
        return end;
    }

    /**
     * @param start
     *            the start to set
     */
    public final void setStart(Date start) {
        Preconditions.checkNotNull(start, "Start date must not be null");
        if (end != null) {
            if (end.compareTo(start) < 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startString = dateFormat.format(start);
                String endString = dateFormat.format(end);
                throw new IllegalArgumentException("Start date must before end date. Given start: " + startString
                                                   + " given end: " + endString);
            }
        }
        this.start = start;
    }

    /**
     * @param end
     *            the end to set
     */
    public final void setEnd(Date end) {
        Preconditions.checkNotNull(end, "End date must not be null.");
        if (start != null) {
            if (end.compareTo(start) < 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startString = dateFormat.format(start);
                String endString = dateFormat.format(end);
                throw new IllegalArgumentException("Start date must before end date. Given start: " + startString
                                                   + " given end: " + endString);
            }
        }
        this.end = end;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((userProject == null) ? 0 : userProject.hashCode());
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
        if (!(obj instanceof Booking)) {
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