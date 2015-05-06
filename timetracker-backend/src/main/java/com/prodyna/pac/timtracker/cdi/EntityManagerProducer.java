package com.prodyna.pac.timtracker.cdi;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Producer of {@link EntityManager}.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public class EntityManagerProducer {

    @Produces
    @PersistenceContext
    private EntityManager entitymanager;

}
