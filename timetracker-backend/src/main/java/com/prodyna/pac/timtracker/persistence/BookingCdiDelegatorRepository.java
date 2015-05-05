package com.prodyna.pac.timtracker.persistence;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import com.prodyna.pac.timtracker.model.Booking;
import com.prodyna.pac.timtracker.model.BookingRepository;

@ApplicationScoped
public class BookingCdiDelegatorRepository implements Repository<Booking> {
    
    @EJB
    private BookingRepository repo;
    
    @Override
    public Class<Booking> getType() {
        return repo.getType();
    }

    @Override
    public Booking store(final Booking entity) {
        return repo.store(entity);
    }

    @Override
    public Booking get(final Long id) {
        return repo.get(id);
    }

    @Override
    public void remove(final Booking entity) {
        repo.remove(entity);
    }

}
