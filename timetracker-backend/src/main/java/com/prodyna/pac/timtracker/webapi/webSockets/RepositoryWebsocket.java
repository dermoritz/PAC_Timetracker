package com.prodyna.pac.timtracker.webapi.webSockets;

import java.io.IOException;
import java.util.function.Consumer;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;

import com.prodyna.pac.timtracker.cdi.UserUtil;
import com.prodyna.pac.timtracker.model.UserRole;
import com.prodyna.pac.timtracker.persistence.Created;
import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.persistence.Removed;
import com.prodyna.pac.timtracker.persistence.Repository;
import com.prodyna.pac.timtracker.persistence.Timestampable;
import com.prodyna.pac.timtracker.webapi.RepresentationConverter;

/**
 * Abstract class to provide generic web socket endpoint for all entities.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 * @param <DOMAIN>
 * @param <REP>
 */
public abstract class RepositoryWebsocket<DOMAIN extends Identifiable & Timestampable, REP extends Identifiable> {

    private Repository<DOMAIN> repo;

    private RepresentationConverter<REP, DOMAIN> converter;

    @Inject
    private UserUtil userUtil;

    @Inject
    private Logger log;
    
    private SessionRegistry sessions;
    
    /**
     * Instantiates this
     * @param sessions session registry
     * @param repo 
     * @param converter 
     */
    public RepositoryWebsocket(SessionRegistry sessions, Repository<DOMAIN> repo, RepresentationConverter<REP, DOMAIN> converter) {
        this.sessions = sessions;
        this.repo = repo;
        this.converter = converter;
    }
    
    /**
     * Registers session and reads config (user name and role).
     * @param session session to be opened
     * @param conf configuration
     */
    @OnOpen
    public void open(Session session, EndpointConfig conf) {
        UserRole role = userUtil.getCreateUser(session.getUserPrincipal().getName(),
                                               (Boolean) conf.getUserProperties()
                                                             .get(WebSocketUserConfig.IS_ADMIN_ADMIN)).getRole();
        if (role.equals(UserRole.ADMIN) || role.equals(UserRole.MANAGER)) {
            sessions.add(session);
        } else {
            try {
                session.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "Permission denied."));
            } catch (IOException e) {
                log.error("failed to close session.");
            }
            log.debug("Rejected to open websocket to user " + session.getUserPrincipal().getName());
        }
    }

    /**
     * Removes session from registered sessions.
     * @param session session that closed
     * @param reason 
     */
    @OnClose
    public void close(Session session, CloseReason reason) {
        sessions.remove(session);
    }

    /**
     * Sends all bookings to all sessions registered to this web socket.
     */
    public void send() {
        sessions.getAll().forEach(new Consumer<Session>() {

            @Override
            public void accept(Session t) {
                t.getAsyncRemote().sendObject(converter.from(null, repo.getAll()));

            }
        });
    }
    
    /**
     * 
     * @param entity created entity
     */
    public void createdEventFired(@Observes @Created DOMAIN entity) {
        send();
    }
    
    /**
     * 
     * @param entity removed entity
     */
    public void removedEventFired(@Observes @Removed DOMAIN entity) {
        send();
    }
    
}
