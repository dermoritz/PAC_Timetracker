package com.prodyna.pac.timtracker.persistence;

/**
 * Defines operations on entities. Update is handled implicitly handled by
 * reading an entity and then updating its state. The peristence provide will
 * commit/synchronize the state.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 * @param <T>
 *            class of entity
 */
public interface Repository<T> {
    /**
     * 
     * @return type of entity
     */
    Class<T> getType();

    /**
     * 
     * @param entity
     *            entity to be stored
     * @return stored entity - id assigned
     */
    T store(T entity);

    /**
     * 
     * @param id
     *            internal id
     * @return entity
     */
    T get(Long id);
    
    /**
     * Removes given entity.
     * @param entity to be removed
     */
    void remove(T entity);

    
}
