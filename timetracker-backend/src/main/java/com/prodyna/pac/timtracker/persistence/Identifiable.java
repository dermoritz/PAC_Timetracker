package com.prodyna.pac.timtracker.persistence;

/**
 * Specifies method for entities with simple primary key.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public interface Identifiable {
    /**
     * 
     * @return id of entity
     */
    Long getId();
}
