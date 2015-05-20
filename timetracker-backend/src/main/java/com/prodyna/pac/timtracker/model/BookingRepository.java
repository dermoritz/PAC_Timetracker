package com.prodyna.pac.timtracker.model;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Typed;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import com.prodyna.pac.timtracker.persistence.PersistenceRepository;

/**
 * Repository for {@link Booking}.
 * 
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

    /**
     * 
     * @param user
     *            the user
     * @return all bookings for given user
     */
    public List<Booking> getBookingsByUser(User user) {
        return getBookingsByUserId(user.getId());
    }
    
    public List<Booking> getBookingsByUserId(Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> createQuery = cb.createQuery(Booking.class);
        Root<Booking> booking = createQuery.from(Booking.class);
        Join<Booking, UsersProjects> join = booking.join(Booking_.userProject, JoinType.INNER);
        Join<UsersProjects, User> userJoin = join.join(UsersProjects_.user);
        createQuery.where(cb.equal(userJoin.get(User_.id), userId));
        createQuery.select(booking);
        return em.createQuery(createQuery).getResultList();
    }
    
    /**
     * 
     * @param project
     *            the user
     * @return all bookings for given user
     */
    public List<Booking> getBookingsByProject(Project project) {
        return getBookingsByProjectId(project.getId());
    }
    
    /**
     * 
     * @param projectId
     * @return all bookings for given project id
     */
    public List<Booking> getBookingsByProjectId(Long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> createQuery = cb.createQuery(Booking.class);
        Root<Booking> booking = createQuery.from(Booking.class);
        Join<Booking, UsersProjects> join = booking.join(Booking_.userProject, JoinType.INNER);
        Join<UsersProjects, Project> projectJoin = join.join(UsersProjects_.project);
        createQuery.where(cb.equal(projectJoin.get(Project_.id), projectId));
        createQuery.select(booking);
        return em.createQuery(createQuery).getResultList();
    }

}
