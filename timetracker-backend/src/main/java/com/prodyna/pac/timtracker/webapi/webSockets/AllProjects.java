package com.prodyna.pac.timtracker.webapi.webSockets;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;

import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.model.ProjectRepository;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.project.ProjectRepresentationConverter;

/**
 * Provides current list of all projects.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@ServerEndpoint(value = "/allprojects", encoders = {JSONEncoder.class}, configurator = WebSocketUserConfig.class)
public class AllProjects extends RepositoryWebsocket<Project, ProjectRepresentation> {

    /**
     * @param sessions session registry
     * @param repo persistence repository
     * @param converter data converter
     */
    @Inject
    public AllProjects(AllProjectsSessionRegistry sessions, ProjectRepository repo,
                    ProjectRepresentationConverter converter) {
        super(sessions, repo, converter);
    }

}
