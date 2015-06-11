package com.prodyna.pac.timtracker.webapi.webSockets;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;

import com.prodyna.pac.timtracker.cdi.UserUtil;
import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.BookingRepository;
import com.prodyna.pac.timtracker.model.UserRole;
import com.prodyna.pac.timtracker.persistence.Created;
import com.prodyna.pac.timtracker.persistence.Removed;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentationConverter;

/**
 * Provides web socket connection to get current list of bookings per project.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@ServerEndpoint(value = "/allbookings/{projectId}", encoders = {JSONEncoder.class},
                configurator = WebSocketUserConfig.class)
public class BookingsByProject {

    @Inject
    private UserUtil userUtil;

    @Inject
    private Logger log;
    
    @Inject
    private BookingRepository repo;
    
    @Inject
    private BookingRepresentationConverter converter;

    /**
     * Project id for this web socket connection.
     */
    private volatile Long projectId = null;

    /**
     * Session for this connection.
     */
    private volatile Session session = null;

    /**
     * Registers session and reads config (user name and role).
     * 
     * @param session
     *            session to be opened
     * @param conf
     *            configuration
     * @param projectId id of project
     */
    @OnOpen
    public void open(Session session, EndpointConfig conf, @PathParam("projectId") String projectId) {
        UserRole role = userUtil.getCreateUser(session.getUserPrincipal().getName(),
                                               (Boolean) conf.getUserProperties()
                                                             .get(WebSocketUserConfig.IS_ADMIN_ADMIN)).getRole();
        try {
            this.projectId = Long.parseLong(projectId);
        } catch (NumberFormatException e) {
            log.debug("rejected connection because given id is not parsable: " + e.getMessage());
            try {
                session.close(new CloseReason(CloseCodes.CANNOT_ACCEPT,
                                              "Problem with given path parameter for project id."));
            } catch (IOException e1) {
                log.error("cant close session, cause: " + e1.getLocalizedMessage());
            }
        }
        if (!role.equals(UserRole.ADMIN) && !role.equals(UserRole.MANAGER)) {
            try {
                session.close(new CloseReason(CloseCodes.VIOLATED_POLICY, "permission denied."));
            } catch (IOException e1) {
                log.error("cant close session, cause: " + e1.getLocalizedMessage());
            }
            log.debug("Rejected to open websocket to user " + session.getUserPrincipal().getName());
        }
        if (session.isOpen()) {
            this.session = session;
        }
    }

    /**
     * Removes session from registered sessions.
     * 
     * @param session
     *            session that closed
     * @param reason
     */
    @OnClose
    public void close(Session session, CloseReason reason) {
        log.debug("Session closed.");
    }

    /**
     * Sends all bookings to all sessions registered to this web socket.
     * @param projectId only 
     */
    public void send(Long projectId) {
        if (session != null && session.isOpen() && this.projectId != null && this.projectId.equals(projectId)) {
            session.getAsyncRemote().sendObject(converter.from(null, repo.getBookingsByProjectId(projectId)));
        }
    }

    /**
     * 
     * @param entity
     *            created entity
     */
    public void createdEventFired(@Observes @Created Booking entity) {
        send(entity.getUserProject().getProject().getId());
    }

    /**
     * 
     * @param entity
     *            removed entity
     */
    public void removedEventFired(@Observes @Removed Booking entity) {
        send(entity.getUserProject().getProject().getId());
    }
}
