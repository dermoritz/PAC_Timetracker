package com.prodyna.pac.timtracker.model;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Typed;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.prodyna.pac.timtracker.persistence.PersistenceRepository;

/**
 * Repository for {@link User}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Stateless
@LocalBean
@Typed(UsersProjectsRepository.class)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UsersProjectsRepository extends PersistenceRepository<UsersProjects> {

    /**
     * Creates {@link PersistenceRepository} with {@link User} as type.
     */
    public UsersProjectsRepository() {
        super(UsersProjects.class);
    }

    /**
     * Retrieves all projects for a given user.
     * 
     * @param user
     *            user
     * @return list of all projects registered to given user
     */
    public List<UsersProjects> getByUser(User user) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UsersProjects> query = cb.createQuery(UsersProjects.class);
        Root<UsersProjects> up = query.from(UsersProjects.class);
        query.where(cb.equal(up.get(UsersProjects_.user), user));
        return em.createQuery(query).getResultList();
    }

    /**
     * 
     * @param project
     *            project
     * @return list with all {@link UsersProjects} for a given project
     */
    public List<UsersProjects> getByProject(Project project) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UsersProjects> query = cb.createQuery(UsersProjects.class);
        Root<UsersProjects> up = query.from(UsersProjects.class);
        query.where(cb.equal(up.get(UsersProjects_.project), project));
        return em.createQuery(query).getResultList();
    }

}
