package com.prodyna.pac.timtracker.model.util;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Helper class to easily setup arquillian container.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public abstract class PersistenceArquillianContainer {
    /**
     * Default container for all tests.
     */
    private static WebArchive container = ShrinkWrap.create(WebArchive.class, "test.war")
                                                    .addAsResource(new StringAsset(PersistenceConfiguration.persistenceDescriptor()
                                                                                                           .exportAsString()),
                                                                   "META-INF/persistence.xml")
                                                    .addAsResource("META-INF/beans.xml")
                                                    .addClass(PersistenceArquillianContainer.class);

    /**
     * Adds classes to default container.
     * 
     * @param classes
     *            classes to add
     * @return container with classes added.
     */
    public static WebArchive addClasses(final Class<?>... classes) {
        return container.addClasses(classes);
    }
    
    /**
     * 
     * @return container
     */
    public static WebArchive get(){
        return container;
    }
}
