package com.prodyna.pac.timtracker.service;

import java.util.List;

import com.prodyna.pac.timtracker.model.Project;

/**
 * Specifies crud methods for {@link Project}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
public interface ProjectService {
    /**
     * Persists given project.
     * 
     * @param project
     *            persists this project
     * @return persisted project with assigned id
     */
    Project create(Project project);

    /**
     * 
     * @return list with all projects.
     */
    List<Project> findAll();

    /**
     * Updates the given project.
     * 
     * @param project
     *            project to be updated
     */
    void update(Project project);

    /**
     * Deletes given project.
     * 
     * @param project
     *            project to be deleted
     */
    void delete(Project project);
}
