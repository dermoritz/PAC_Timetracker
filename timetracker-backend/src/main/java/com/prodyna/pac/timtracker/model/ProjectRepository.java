package com.prodyna.pac.timtracker.model;

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

}
