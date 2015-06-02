package com.prodyna.pac.timtracker.webapi.security;

import com.prodyna.pac.timtracker.model.User;

/**
 * {@link SecurityInterceptor} will only permit resources implementing this
 * interface.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public interface SecureResource {
    /**
     * 
     * @param user
     *            user that requested the resource
     * @param url
     *            resource requested
     * @param string
     *            http method that was called
     * @return true if user is permitted to request given url, false otherwise
     */
    boolean permit(User user, String url, String string);
}
