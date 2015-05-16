package com.prodyna.pac.timtracker.persistence;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRepository;


@ApplicationScoped
public class UserCdiDelegatorRepository implements Repository<User> {
    
    @EJB
    private UserRepository repo;
    
    @Override
    public Class<User> getType() {
        return repo.getType();
    }

    @Override
    public User store(User entity) {
        return repo.store(entity);
    }

    @Override
    public User get(Long id) {
        return repo.get(id);
    }

    @Override
    public void remove(User entity) {
        repo.remove(entity);
    }

    @Override
    public List<User> getAll() {
        return repo.getAll(); 
    }

    @Override
    public List<User> getAllPaginated(int pageNumber, int pageSize) {
        return repo.getAllPaginated(pageNumber, pageSize); 
    }
    
}
