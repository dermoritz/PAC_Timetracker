package com.prodyna.pac.timtracker.webapi.resource.users_projects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
    public UsersProjectsRepresentation from(UsersProjects source) {
        UsersProjectsRepresentation usersProjectsRep = new UsersProjectsRepresentation();
        usersProjectsRep.setId(source.getId());
        usersProjectsRep.setProject(proRepConv.from(source.getProject()));
        usersProjectsRep.setUser(uRepConv.from(source.getUser()));
        return usersProjectsRep;
    }

    @Override
    public UsersProjects createNew(UsersProjectsRepresentation representation) {
        return new UsersProjects(uRepConv.to(representation.getUser()),
                                 proRepConv.to(representation.getProject()));
    }

    @Override
    public UsersProjects update(UsersProjectsRepresentation representation, UsersProjects target) {
        // usersProjects are immutable
        return target;
    }

}
