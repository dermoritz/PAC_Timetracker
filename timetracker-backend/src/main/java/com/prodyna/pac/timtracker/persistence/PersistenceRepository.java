package com.prodyna.pac.timtracker.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Abstract implementation of {@link Repository} using JPA.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 * @param <T>
 */
public abstract class PersistenceRepository<T> implements Repository<T> {
    
    /**
     * Entity manager - provides JPA.
     */
    @PersistenceContext
    private EntityManager em;
    
    /**
     * Type of this entity.
     */
    private Class<T> type;
    
    /**
     * 
     * @param type type of entity
     */
    public PersistenceRepository(final Class<T> type) {
        this.type = type;
    }
    
    @Override
    public final Class<T> getType() {
        return type;
    }

    @Override
    public final T store(final T entity) {
        T mergedEntity = merge(entity);
        em.persist(mergedEntity);
        return mergedEntity;
    }

    @Override
    public final T get(final Object id) {
        return em.find(type, id);
    }
    
    @Override
    public final void remove(final T entity) {
        em.remove(merge(entity));
    }

    /**
     * Calls {@link EntityManager#merge(Object)} on given entity and returns it.
     * @param entity to be merged
     * @return merged entity
     */
    private T merge(final T entity) {
        return em.merge(entity);
    }
    
}
