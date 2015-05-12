package com.prodyna.pac.timtracker.cdi;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produces {@link Logger}
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public class LoggerProducer {

    /**
     * 
     * @param ip
     *            used to name the logger returned
     * @return named logger
     */
    @Produces
    public Logger loggerProducer(final InjectionPoint ip) {
        return LoggerFactory.getLogger(ip.getMember().getDeclaringClass());
    }

}
