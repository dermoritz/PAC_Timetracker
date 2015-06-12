package com.prodyna.pac.timtracker.webapi.webSockets;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRepository;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.user.UserRepresentationConverter;

/**
 * Provides current list of all users.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@ServerEndpoint(value = "/allusers", encoders = {JSONEncoder.class}, configurator = WebSocketUserConfig.class)
public class AllUsers extends RepositoryWebsocket<User, UserRepresentation> {

    /**
     * @param sessions session registry
     * @param repo persistence repository
     * @param converter data converter
     */
    @Inject
    public AllUsers(AllUsersSessionRegistry sessions, UserRepository repo,
                    UserRepresentationConverter converter) {
        super(sessions, repo, converter);
    }

}
