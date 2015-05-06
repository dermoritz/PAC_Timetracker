package com.prodyna.pac.timtracker.persistence;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRepository;
import com.prodyna.pac.timtracker.model.UsersProjects;
import com.prodyna.pac.timtracker.model.UsersProjectsRepository;


@ApplicationScoped
public class UsersProjectsCdiDelegatorRepository implements Repository<UsersProjects> {
    
    @EJB
    private UsersProjectsRepository repo;
    
    @Override
    public Class<UsersProjects> getType() {
        return repo.getType();
    }

    @Override
    public UsersProjects store(UsersProjects entity) {
        return repo.store(entity);
    }

    @Override
    public UsersProjects get(Long id) {
        return repo.get(id);
    }

    @Override
    public void remove(UsersProjects entity) {
        repo.remove(entity);
    }
    
}
