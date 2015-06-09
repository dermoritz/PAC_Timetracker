package com.prodyna.pac.timtracker.model;

import java.util.Date;
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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
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
     * @param userId id of user
     * @param start start time
     * @param end end time
     * @return List of bookings with overlapping interval for given user and
     *         given start and end.
     */
    public List<Booking> getOverlapping(Long userId, Date start, Date end) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> query = cb.createQuery(Booking.class);
        Root<Booking> booking = query.from(Booking.class);
        Join<Booking, UsersProjects> join = booking.join(Booking_.userProject, JoinType.INNER);
        Join<UsersProjects, User> userJoin = join.join(UsersProjects_.user);
        ParameterExpression<Date> startP = cb.parameter(Date.class);
        ParameterExpression<Date> endP = cb.parameter(Date.class);

        Predicate userIdIsEqual = cb.equal(userJoin.get(User_.id), userId);
        Predicate endAfterStart = cb.greaterThan(booking.get(Booking_.end), startP);
        Predicate startBeforeEnd = cb.lessThan(booking.get(Booking_.start), endP);

        query.where(cb.and(userIdIsEqual, cb.and(endAfterStart, startBeforeEnd)));
        query.select(booking);
        return em.createQuery(query).setParameter(startP, start).setParameter(endP, end).getResultList();
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
