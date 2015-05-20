package com.prodyna.pac.timtracker.model;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Typed;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
        return getByUserId(user.getId());
    }

    /**
     * 
     * @param project
     *            project
     * @return list with all {@link UsersProjects} for a given project
     */
    public List<UsersProjects> getByProject(Project project) {
        return getByProjectId(project.getId());
    }

    public List<UsersProjects> getByUserId(Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UsersProjects> query = cb.createQuery(UsersProjects.class);
        Root<UsersProjects> up = query.from(UsersProjects.class);
        Join<UsersProjects, User> join = up.join(UsersProjects_.user, JoinType.INNER);
        query.where(cb.equal(join.get(User_.id), userId));
        return em.createQuery(query).getResultList();
    }

    public List<UsersProjects> getByProjectId(Long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UsersProjects> query = cb.createQuery(UsersProjects.class);
        Root<UsersProjects> up = query.from(UsersProjects.class);
        Join<UsersProjects, Project> join = up.join(UsersProjects_.project, JoinType.INNER);
        query.where(cb.equal(join.get(Project_.id), projectId));
        return em.createQuery(query).getResultList();
    }

}
