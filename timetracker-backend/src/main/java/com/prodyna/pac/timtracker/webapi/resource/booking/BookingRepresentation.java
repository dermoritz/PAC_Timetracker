package com.prodyna.pac.timtracker.webapi.resource.booking;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.persistence.Identifiable;
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
    
    public BookingRepresentation() {
        this(null);
    }

    public BookingRepresentation(UriInfo uriInfo) {
        super(uriInfo);
    }
    
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
    
    
}
