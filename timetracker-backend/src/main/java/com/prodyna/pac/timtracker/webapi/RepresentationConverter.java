package com.prodyna.pac.timtracker.webapi;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.UriInfo;

/**
 * Specifies how to get representation (rest) for a given source type and vice
 * versa.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 * @param <REST>
 * @param <SOURCE>
 */
public interface RepresentationConverter<REST, SOURCE> {

    /**
     * 
     * @return representation class for this' entity class
     */
    Class<REST> getRepresentationClass();

    /**
     * 
     * @return source class for this' representation class
     */
    Class<SOURCE> getSourceClass();

    /**
     * Returns rest representation for given entity based on uri info.
     * 
     * @param uriInfo
     *            uri target
     * @param source
     *            entity in db
     * @return rest representation
     */
    REST from(UriInfo uriInfo, SOURCE source);

    /**
     * Returns a collection of rest data for given collection of db-data.
     * 
     * @param uriInfo
     *            uri target
     * @param sources
     *            collection of db entities
     * @return rest representation of given entities
     */
    Collection<REST> from(UriInfo uriInfo, Collection<SOURCE> sources);

    SOURCE to(UriInfo uriInfo, REST representation);

    SOURCE update(UriInfo uriInfo, REST representation, SOURCE target);

    Collection<SOURCE> to(UriInfo uriInfo, Collection<REST> representations);

    /**
     * Base implementation for converter.
     * 
     * @author moritz löser (moritz.loeser@prodyna.com)
     *
     * @param <REST>
     * @param <SOURCE>
     */
    public abstract static class Base<REST, SOURCE> implements RepresentationConverter<REST, SOURCE> {

        private Class<REST> representationClass;
        private Class<SOURCE> sourceClass;

        protected Base() {
        }

        /**
         * 
         * @param representationClass
         *            target
         * @param sourceClass
         *            source
         */
        public Base(Class<REST> representationClass, Class<SOURCE> sourceClass) {
            this.representationClass = representationClass;
            this.sourceClass = sourceClass;
        }

        @Override
        public Class<REST> getRepresentationClass() {
            return representationClass;
        }

        @Override
        public Class<SOURCE> getSourceClass() {
            return sourceClass;
        }

        @Override
        public Collection<REST> from(UriInfo uriInfo, Collection<SOURCE> ins) {
            Collection<REST> out = new ArrayList<REST>();
            for (SOURCE in : ins) {
                out.add(from(uriInfo, in));
            }
            return out;
        }

        @Override
        public Collection<SOURCE> to(UriInfo uriInfo, Collection<REST> ins) {
            Collection<SOURCE> out = new ArrayList<SOURCE>();
            for (REST in : ins) {
                out.add(to(uriInfo, in));
            }
            return out;
        }
    }
}
