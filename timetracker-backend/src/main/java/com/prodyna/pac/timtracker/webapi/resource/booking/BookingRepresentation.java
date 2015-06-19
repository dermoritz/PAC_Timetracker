package com.prodyna.pac.timtracker.webapi.resource.booking;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.webapi.Linkable;
import com.prodyna.pac.timtracker.webapi.resource.users_projects.UsersProjectsRepresentation;

/**
 * DTO class for {@link Booking}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@XmlRootElement(name = "booking", namespace = "urn:timetracker:booking")
public class BookingRepresentation extends Linkable {

    @NotNull
    private Date start;
    
    @NotNull
    private Date end;
    
    @NotNull
    private UsersProjectsRepresentation usersProjects;
    
    /**
     * @return the start
     */
    public Date getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * @return the usersProjects
     */
    public UsersProjectsRepresentation getUsersProjects() {
        return usersProjects;
    }

    /**
     * @param usersProjects the usersProjects to set
     */
    public void setUsersProjects(UsersProjectsRepresentation usersProjects) {
        this.usersProjects = usersProjects;
    }

    @Override
    protected Class<?> getResourceClass() {
        return BookingResource.class; 
    }

    @Override
    public String toString() {
        return "BookingRepresentation [getStart()=" + getStart() + ", getEnd()=" + getEnd() + ", getUsersProjects()="
               + getUsersProjects() + ", getId()=" + getId() + ", getSelf()=" + getSelf() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((usersProjects == null) ? 0 : usersProjects.hashCode());
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
        if (!(obj instanceof BookingRepresentation)) {
            return false;
        }
        BookingRepresentation other = (BookingRepresentation) obj;
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
        if (usersProjects == null) {
            if (other.usersProjects != null) {
                return false;
            }
        } else if (!usersProjects.equals(other.usersProjects)) {
            return false;
        }
        return true;
    }
    
}
