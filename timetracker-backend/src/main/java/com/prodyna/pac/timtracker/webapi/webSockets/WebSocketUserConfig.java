package com.prodyna.pac.timtracker.webapi.webSockets;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import com.prodyna.pac.timtracker.model.UserRole;

/**
 * Extracts user information from request and provides it to websockets.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public class WebSocketUserConfig extends ServerEndpointConfig.Configurator {
    /**
     * Key used in config to mark if requesting user is admin.
     */
    public static final String IS_ADMIN_ADMIN = "isAdmin";

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        config.getUserProperties().put(IS_ADMIN_ADMIN, request.isUserInRole(UserRole.ADMIN.name()));
        super.modifyHandshake(config, request, response);
    }
    
}
