package com.prodyna.pac.timtracker.webapi.webSockets;

import java.util.function.Consumer;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.prodyna.pac.timtracker.cdi.CurrentUser;
import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.BookingRepository;
import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.persistence.Created;
import com.prodyna.pac.timtracker.persistence.Removed;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentationConverter;

@ServerEndpoint(value = "/allbookings", encoders = {JSONEncoder.class})
public class AllBookings {

    @Inject
    private BookingRepository repo;

    @Inject
    private BookingRepresentationConverter converter;

    @Inject
    private SessionRegistry sessions;

    @OnOpen
    public void open(Session session, EndpointConfig conf) {
        sessions.add(session);
    }

    @OnClose
    public void close(Session session, CloseReason reason) {
        sessions.remove(session);
    }
    
    public void send(){
        sessions.getAll().forEach(new Consumer<Session>() {

            @Override
            public void accept(Session t) {
                t.getAsyncRemote().sendObject(converter.from(null, repo.getAll()));
                
            }
        });
    }
    
    public void createdEventFired(@Observes @Created Booking booking) {
        send();
    }

    public void removedEventFired(@Observes @Removed Booking booking) {
        send();
    }
    
}
