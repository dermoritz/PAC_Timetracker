package com.prodyna.pac.timtracker.persistence;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import com.prodyna.pac.timtracker.model.Project;
import com.prodyna.pac.timtracker.model.ProjectRepository;
import com.prodyna.pac.timtracker.model.User;
@ApplicationScoped
public class ProjectCdiDelegatorRepository implements Repository<Project> {
    
    @EJB
    private ProjectRepository repo;
    
    @Override
    public Class<Project> getType() {
        return repo.getType();
    }

    @Override
    public Project store(final Project entity) {
        return repo.store(entity);
    }

    @Override
    public Project get(final Long id) {
        return repo.get(id);
    }

    @Override
    public void remove(final Project entity) {
        repo.remove(entity);
    }

    @Override
    public List<Project> getAll() {
        return repo.getAll(); 
    }

    @Override
    public List<Project> getAllPaginated(int pageNumber, int pageSize) {
        return repo.getAllPaginated(pageNumber, pageSize); 
    }
    
    
    
}
