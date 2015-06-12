package com.prodyna.pac.timtracker.webapi.webSockets;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;

import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.BookingRepository;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentation;
import com.prodyna.pac.timtracker.webapi.resource.booking.BookingRepresentationConverter;

/**
 * Provides current list of all bookings.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@ServerEndpoint(value = "/allbookings", encoders = {JSONEncoder.class}, configurator = WebSocketUserConfig.class)
public class AllBookings extends RepositoryWebsocket<Booking, BookingRepresentation> {

    @Inject
    public AllBookings(AllBookingsSessionRegistry sessions, BookingRepository repo,
                       BookingRepresentationConverter converter) {
        super(sessions, repo, converter);
    }

}
