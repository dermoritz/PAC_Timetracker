package com.prodyna.pac.timtracker.persistence;

import java.util.List;

import javax.persistence.metamodel.SingularAttribute;

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
     * 
     * @param entity
     *            to be removed
     */
    void remove(T entity);

    /**
     * 
     * @return list with all entities.
     */
    List<T> getAll();

    /**
     * Used for paginated queries. Parameter 2,10 will return entries from 11 to
     * 20.
     * 
     * @param pageNumber
     *            number of page
     * @param pageSize
     *            entires per page
     * @return list with all entries for given "page" and entries per page
     */
    List<T> getAllPaginated(int pageNumber, int pageSize);

}
