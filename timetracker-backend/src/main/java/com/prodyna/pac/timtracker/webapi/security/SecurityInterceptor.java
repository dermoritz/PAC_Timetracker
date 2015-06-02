package com.prodyna.pac.timtracker.webapi.security;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;
import com.prodyna.pac.timtracker.cdi.CurrentUser;
import com.prodyna.pac.timtracker.model.User;

/**
 * Secures the webapi by intercepting all requests and checking permissions. Is
 * automatically applied to all resources by JAX-RS 2 mechanics.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityInterceptor implements ContainerRequestFilter {
    
    @Inject
    @CurrentUser
    private User currentUser;

    @Context
    private UriInfo uriInfo;
    
    @Inject
    private Logger log;
    
    /**
     * Checks injections.
     */
    @PostConstruct
    public void check(){
        Preconditions.checkNotNull(currentUser, "Current user must not be null.");
        Preconditions.checkNotNull(uriInfo, "UriInfo must not be null.");
    }
    
    
    private static final Status REJECT_STATUS = Status.NOT_FOUND;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        List<Object> matchedResources = uriInfo.getMatchedResources();
        // before relying to resources do some checks
        if (matchedResources.isEmpty()) {
            requestContext.abortWith(Response.status(REJECT_STATUS).build());
            log.debug("Rejected request, no resource matched.");
        }
        else if (matchedResources.size() > 1) {
            requestContext.abortWith(Response.status(REJECT_STATUS).build());
            log.debug("Rejected request, more than 1 resource matched.");
        }
        // most important check, only requests to "SecureResources" will be
        // relayed
        else if (!(matchedResources.get(0) instanceof SecureResource)) {
            requestContext.abortWith(Response.status(REJECT_STATUS).build());
            log.debug("Rejected request. Requested resource " + matchedResources.get(0).getClass().getName()
                      + " does not implement SecureResource.");
        } else {
            // should be safe now
            SecureResource resource = (SecureResource) matchedResources.get(0);
            // the actual check is done by resource
            if (!resource.permit(currentUser, uriInfo.getPath(), requestContext.getMethod())) {
                requestContext.abortWith(Response.status(REJECT_STATUS).build());
                log.debug("Rejected request to \"" + uriInfo.getPath() + "\" for " + currentUser);
            }
        }
    }
}
