package com.prodyna.pac.timtracker.webapi.resource.project;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.webapi.RepositoryResource;
import com.prodyna.pac.timtracker.webapi.interceptor.Linkable;

/**
 * Crud rest resource for {@link Project}.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Path("/project")
@RequestScoped
@Linkable
public class ProjectResource extends RepositoryResource<Project, ProjectRepresentation> {

    
    /**
     * User specific XML {@link MediaType}. 
     */
    public static final String PROJECT_XML_MEDIA_TYPE = MediaType.APPLICATION_XML + "; type=project";
    
    /**
     * User specific JSON {@link MediaType}.
     */
    public static final String PROJECT_JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON + "; type=project";
    
    /**
     * Creates {@link RepositoryResource} typed for user.
     */
    public ProjectResource() {
        super(ProjectResource.class, Project.class, ProjectRepresentation.class);
    }

    @Override
    public String getResourceMediaType() {
        return PROJECT_XML_MEDIA_TYPE;
    }

    @Override
    protected String[] getMediaTypes() {
        return new String[] {PROJECT_XML_MEDIA_TYPE, PROJECT_JSON_MEDIA_TYPE};
    }
}
