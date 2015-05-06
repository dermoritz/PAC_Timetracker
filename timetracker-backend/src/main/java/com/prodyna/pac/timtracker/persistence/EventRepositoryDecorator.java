package com.prodyna.pac.timtracker.persistence;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

/**
 * Fires events on store and remove operations on entities.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 * @param <T> entity
 */
@Decorator
public abstract class EventRepositoryDecorator<T extends Identifiable> implements Repository<T> {
    
    /**
     * Event to be fired.
     */
    @Inject
    private Event<Identifiable> event;
    
    /**
     * This decorates {@link Repository}.
     */
    @Inject @Any @Delegate
    private Repository<T> delegate;

    /**
     * Decoration for store, fires {@link Created} event.
     */
    @Override
    public T store(T entity) {
        T stored = delegate.store(entity);
        event.select(delegate.getType(), new Created.Literal()).fire(entity);
        return stored;
    }
    
    /**
     * Decoration for remove, fires {@link Removed} event.
     */
    @Override
    public void remove(T entity) {
        delegate.remove(entity);
        event.select(delegate.getType(), new Removed.Literal()).fire(entity);
    }
}
