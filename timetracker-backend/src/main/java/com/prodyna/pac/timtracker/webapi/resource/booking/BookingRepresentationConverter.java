package com.prodyna.pac.timtracker.webapi.resource.booking;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;

import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.UsersProjects;
import com.prodyna.pac.timtracker.webapi.RepresentationConverter;
import com.prodyna.pac.timtracker.webapi.resource.users_projects.UsersProjectsRepresentationConverter;

/**
 * Converts {@link Booking} to {@link BookingRepresentation} and vice versa.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RequestScoped
public class BookingRepresentationConverter extends RepresentationConverter.Base<BookingRepresentation, Booking> {
    
    @Inject
    private UsersProjectsRepresentationConverter upRepConv;

    @Override
    public BookingRepresentation from(UriInfo uriInfo, Booking source) {
        BookingRepresentation bRep = new BookingRepresentation();
        bRep.setEnd(source.getEnd());
        bRep.setStart(source.getStart());
        bRep.setId(source.getId());
        bRep.setUsersProjects(upRepConv.from(uriInfo, source.getUserProject()));
        return bRep;
    }

    @Override
    public Booking createNew(UriInfo uriInfo, BookingRepresentation representation) {
        //create new objects
        UsersProjects usersProjects = upRepConv.to(uriInfo, representation.getUsersProjects());
        return new Booking(usersProjects, representation.getStart(), representation.getEnd());
    }

    @Override
    public Booking update(UriInfo uriInfo, BookingRepresentation representation, Booking target) {
        target.setEnd(representation.getEnd());
        target.setStart(representation.getStart());
        return target;
    }

}
