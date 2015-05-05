package com.prodyna.pac.timtracker.persistence;

import java.util.Date;

/**
 * Specifies methods for entities to hold created and last modified date.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public interface Timestampable {
    /**
     * 
     * @return creation date of this entity
     */
    Date getCreated();

    /**
     * 
     * @return the date when this entity was last modified (creation date if
     *         never updated)
     */
    Date getLastModified();
}
