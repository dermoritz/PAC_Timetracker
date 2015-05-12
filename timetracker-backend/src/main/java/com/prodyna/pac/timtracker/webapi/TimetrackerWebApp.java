package com.prodyna.pac.timtracker.webapi;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Web api entry point.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@ApplicationPath("timetracker")
public class TimetrackerWebApp extends Application {
    //marker class to signal use of jax rs
}
