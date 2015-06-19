package com.prodyna.pac.timtracker.cdi;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Renders {@link UriInfo} injectable via cdi.
 * 
 * @author moritz
 *
 */
@RequestScoped
@Provider
public class UriInfoProducer  implements Serializable, ContextResolver<UriInfo>{
	
	/**
	 * Default. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Context
	private UriInfo uriInfo;

	@Produces
	public UriInfo get() {
		return uriInfo;
	}

	@Override
	public UriInfo getContext(Class<?> type) {
		return uriInfo;
	}

}
