package com.prodyna.pac.timtracker.webapi.resource.project;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.UriInfo;

import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.webapi.RepresentationConverter;

/**
 * Converts {@link ProjectRepresentation} to {@link Project} an vice versa.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RequestScoped
public class ProjectRepresentationConverter extends RepresentationConverter.Base<ProjectRepresentation, Project> {

    @Override
    public ProjectRepresentation from(UriInfo uriInfo, Project source) {
        ProjectRepresentation projectRepresentation = new ProjectRepresentation();
        projectRepresentation.setDescription(source.getDescription());
        projectRepresentation.setId(source.getId());
        projectRepresentation.setName(source.getName());
        return projectRepresentation;
    }

    @Override
    public Project createNew(UriInfo uriInfo, ProjectRepresentation representation) {
        return new Project(representation.getName(), representation.getDescription());
    }

    @Override
    public Project update(UriInfo uriInfo, ProjectRepresentation representation, Project target) {
        target.setDescription(representation.getDescription());
        return target;
    }

}
