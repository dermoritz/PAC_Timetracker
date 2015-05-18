package com.prodyna.pac.timtracker.model;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Typed;

import com.prodyna.pac.timtracker.persistence.PersistenceRepository;
/**
 * Repository for {@link Project}.
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Stateless
@LocalBean
@Typed(ProjectRepository.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ProjectRepository extends PersistenceRepository<Project> {
    
    /**
     * Creates {@link PersistenceRepository} with {@link Project} as type.
     */
    public ProjectRepository() {
        super(Project.class);
    }
    
    /**
     * Queries projects by name (case insensitive).
     * 
     * @param name
     *            project name
     * @return project with given name
     */
    public Project getByName(String name) {
        List<Project> results = getByStringAttribute(Project_.name, name);
        // since name is a key there should be either one or zero results.
        if (results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

}
