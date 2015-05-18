package com.prodyna.pac.timtracker.persistence;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.User_;

/**
 * Abstract implementation of {@link Repository} using JPA.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 * @param <T>
 */
public abstract class PersistenceRepository<T> implements Repository<T> {

    /**
     * Entity manager - provides JPA.
     */
    @Inject
    protected EntityManager em;

    /**
     * Type of this entity.
     */
    private Class<T> type;

    /**
     * 
     * @param type
     *            type of entity
     */
    public PersistenceRepository(final Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public T store(final T entity) {
        T mergedEntity = merge(entity);
        em.persist(mergedEntity);
        return mergedEntity;
    }

    @Override
    public T get(final Long id) {
        return em.find(type, id);
    }

    @Override
    public void remove(final T entity) {
        em.remove(merge(entity));
    }

    @Override
    public List<T> getAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> rootEntry = cq.from(type);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = em.createQuery(all);
        return allQuery.getResultList();
    }

    @Override
    public List<T> getAllPaginated(int pageNumber, int pageSize) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> rootEntry = cq.from(type);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = em.createQuery(all);
        allQuery.setFirstResult((pageNumber - 1) * pageSize);
        allQuery.setMaxResults(pageSize);
        return allQuery.getResultList();
    }
    
    /**
     * Performs a case insensitive query for given attribute and value. 
     * @param attribute
     * @param value
     * @return
     */
    protected List<T> getByStringAttribute(SingularAttribute<? super T, String> attribute, String value){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(getType());
        Root<T> user = query.from(getType());
        query.where(cb.equal(cb.lower(user.get(attribute)), value.toLowerCase()));
        return em.createQuery(query).getResultList();
    }
    
    /**
     * Calls {@link EntityManager#merge(Object)} on given entity and returns it.
     * 
     * @param entity
     *            to be merged
     * @return merged entity
     */
    private T merge(final T entity) {
        return em.merge(entity);
    }
}
