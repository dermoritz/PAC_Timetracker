package com.prodyna.pac.timtracker.model;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Typed;

import com.prodyna.pac.timtracker.persistence.PersistenceRepository;

/**
 * Repository for {@link Booking}.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Stateless
@LocalBean
@Typed(BookingRepository.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BookingRepository extends PersistenceRepository<Booking> {
    
    /**
     * Constructs {@link PersistenceRepository} and setting this' class as type.
     */
    public BookingRepository() {
        super(Booking.class);
    }

}
