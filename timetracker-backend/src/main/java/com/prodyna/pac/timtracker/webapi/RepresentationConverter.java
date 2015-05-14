package com.prodyna.pac.timtracker.webapi;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.persistence.criteria.Fetch;
import javax.ws.rs.core.UriInfo;

import com.prodyna.pac.timtracker.persistence.Identifiable;
import com.prodyna.pac.timtracker.persistence.Repository;

/**
 * Specifies how to get representation (rest) for a given source type and vice
 * versa.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 * @param <REST>
 * @param <SOURCE>
 */
public interface RepresentationConverter<REST extends Identifiable, SOURCE> {

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

    /**
     * Converts a rest representation (dto) into entity object.
     * 
     * @param uriInfo
     *            info from request
     * @param representation
     *            dto
     * @return entity object
     */
    SOURCE to(UriInfo uriInfo, REST representation);

    /**
     * Updates given entity with data from dto.
     * 
     * @param uriInfo
     *            info from request
     * @param representation
     *            dto
     * @param target
     *            entity to be updated
     * @return updated entity
     */
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
    public abstract static class Base<REST extends Identifiable, SOURCE> implements
                                                                         RepresentationConverter<REST, SOURCE> {

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

        @Inject
        private Repository<SOURCE> repo;

        /**
         * If given representation has id it will return the entity with this id
         * fetched from data base. Other wise it will delegate to subclass to
         * let it create a new instance.
         */
        @Override
        public SOURCE to(UriInfo uriInfo, REST representation) {
            Long id = representation.getId();
            if (id != null) {
                return repo.get(id);
            } else {
                return createNew(uriInfo, representation);
            }

        }

        /**
         * Creates new instance of entity based on given representation.
         * 
         * @param uriInfo
         *            data from uri
         * @param representation
         *            dto
         * @return new instance of entity
         */
        protected abstract SOURCE createNew(UriInfo uriInfo, REST representation);

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
