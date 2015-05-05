package com.prodyna.pac.timtracker.model.util;

import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.persistence10.PersistenceDescriptor;

public final class PersistenceConfiguration {

    private PersistenceConfiguration() {
        throw new AssertionError("You shall not call this!");
    }

    public static PersistenceDescriptor persistenceDescriptor() {
        return Descriptors.create(PersistenceDescriptor.class)
                          .createPersistenceUnit()
                          .name("test")
                          .getOrCreateProperties()
                              .createProperty()
                                  .name("hibernate.hbm2ddl.auto")
                                  .value("create-drop").up()
                              .createProperty()
                                  .name("hibernate.show_sql")
                                  .value("true").up().up()
                          .jtaDataSource("java:jboss/datasources/ExampleDS").up();
    }
}
