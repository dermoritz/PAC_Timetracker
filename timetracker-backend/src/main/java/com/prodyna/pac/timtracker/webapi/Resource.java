package com.prodyna.pac.timtracker.webapi;

/**
 * Interface for rest-addressable resources.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public interface Resource {
    /**
     * 
     * @return type of resource
     */
    Class<? extends Resource> getResourceClass();

    /**
     * 
     * @return media type of resource
     */
    String getResourceMediaType();
}
