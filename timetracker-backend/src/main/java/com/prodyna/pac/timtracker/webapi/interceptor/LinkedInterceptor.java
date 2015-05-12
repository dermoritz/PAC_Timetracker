package com.prodyna.pac.timtracker.webapi.interceptor;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import com.prodyna.pac.timtracker.webapi.LinkProvider;
import com.prodyna.pac.timtracker.webapi.LinkableRepresentation;

/**
 * Interceptor that add links to other resources.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Linkable
@Provider
@Priority(value = Priorities.ENTITY_CODER)
public class LinkedInterceptor implements WriterInterceptor {
    
    @Inject
    private Instance<LinkProvider> linkProviers;
    
    @Override
    public void aroundWriteTo(WriterInterceptorContext ic) throws WebApplicationException, IOException {
        Object obj = ic.getEntity();
        
        if (hasLinkableRepresentations(obj)) {
            linkRepresentations(obj);
        } else if (hasListLinkableRepresentations(obj)) {
            linkAllRepresentations(obj);
        }
        ic.proceed();
    }

    private boolean hasLinkableRepresentations(Object obj) {
        return locateLinkableRepresentations(obj) != null;
    }

    private boolean hasListLinkableRepresentations(Object obj) {
        return locateLinkableListRepresentations(obj) != null;
    }

    private LinkableRepresentation<?> locateLinkableRepresentations(Object obj) {
        if (obj instanceof Response) {
            Object entity = ((Response) obj).getEntity();
            if (entity instanceof LinkableRepresentation) {
                return (LinkableRepresentation<?>) entity;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Collection<LinkableRepresentation<?>> locateLinkableListRepresentations(Object obj) {
        if (obj instanceof Response) {
            Object entity = ((Response) obj).getEntity();
            if (entity instanceof Collection) {
                Collection<?> objCollection = (Collection<?>) entity;
                if (objCollection.size() > 0 && objCollection.iterator().next() instanceof LinkableRepresentation) {
                    return (Collection<LinkableRepresentation<?>>) objCollection;
                }
            } else if (entity instanceof GenericEntity) {
                GenericEntity<?> genericEntity = (GenericEntity<?>) entity;
                if (genericEntity.getEntity() instanceof Collection) {
                    Collection<?> objCollection = (Collection<?>) genericEntity.getEntity();
                    if (objCollection.size() > 0 && objCollection.iterator().next() instanceof LinkableRepresentation) {
                        return (Collection<LinkableRepresentation<?>>) objCollection;
                    }
                }
            }
        }
        return null;
    }

    private void linkRepresentations(Object obj) {
        LinkableRepresentation<?> linkable = locateLinkableRepresentations(obj);
        link(linkable);
    }

    private void linkAllRepresentations(Object obj) {
        Collection<LinkableRepresentation<?>> linkables = locateLinkableListRepresentations(obj);
        for (LinkableRepresentation<?> linkable : linkables) {
            link(linkable);
        }
    }

    private void link(LinkableRepresentation<?> linkable) {
        for (LinkProvider linker : linkProviers) {
            linker.appendLinks(linkable);
        }
    }
}
