package com.prodyna.pac.timtracker.webapi;

/**
 * Used to model path between different media/data types.
 * E.g. from user you can get to his projects.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 * @param <T> source type
 */
public interface Representation<T> {
    /**
     * 
     * @return type of source
     */
    Class<T> getSourceType();
    
    /**
     * 
     * @return type of data returned.
     */
    String getRepresentationType();
}
