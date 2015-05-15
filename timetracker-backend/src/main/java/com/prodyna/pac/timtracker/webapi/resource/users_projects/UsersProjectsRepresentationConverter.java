package com.prodyna.pac.timtracker.webapi.resource.users_projects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.UriInfo;

import com.prodyna.pac.timtracker.model.UsersProjects;
import com.prodyna.pac.timtracker.webapi.RepresentationConverter;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentationConverter;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentationConverter;

/**
 * Converts {@link UsersProjectsRepresentation} to {@link UsersProjects} and
 * vice versa.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RequestScoped
public class UsersProjectsRepresentationConverter
                                                 extends
                                                 RepresentationConverter.Base<UsersProjectsRepresentation, UsersProjects> {

    @Inject
    private ProjectRepresentationConverter proRepConv;

    @Inject
    private UserRepresentationConverter uRepConv;
        
    @Override
    public UsersProjectsRepresentation from(UriInfo uriInfo, UsersProjects source) {
        UsersProjectsRepresentation usersProjectsRep = new UsersProjectsRepresentation(uriInfo);
        usersProjectsRep.setId(source.getId());
        usersProjectsRep.setProject(proRepConv.from(uriInfo, source.getProject()));
        usersProjectsRep.setUser(uRepConv.from(uriInfo, source.getUser()));
        return usersProjectsRep;
    }

    @Override
    public UsersProjects createNew(UriInfo uriInfo, UsersProjectsRepresentation representation) {
        return new UsersProjects(uRepConv.to(uriInfo, representation.getUser()),
                                 proRepConv.to(uriInfo, representation.getProject()));
    }

    @Override
    public UsersProjects update(UriInfo uriInfo, UsersProjectsRepresentation representation, UsersProjects target) {
        // usersProjects are immutable
        return target;
    }

}
