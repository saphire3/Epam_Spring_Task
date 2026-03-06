package com.epam.training.dao;

import com.epam.training.model.Trainee;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDao {

    private final SessionFactory sessionFactory;

    public TraineeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Trainee trainee) {
        sessionFactory.getCurrentSession().persist(trainee);
    }

    public Trainee update(Trainee trainee) {
        return (Trainee) sessionFactory.getCurrentSession().merge(trainee);
    }

    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Trainee.class, id));
    }

    public Optional<Trainee> findByUsername(String username) {
        return sessionFactory.getCurrentSession()
                .createQuery("""
                        select t
                        from Trainee t
                        join t.user u
                        where u.username = :username
                        """, Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public List<Trainee> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Trainee", Trainee.class)
                .getResultList();
    }

    public void delete(Trainee trainee) {
        sessionFactory.getCurrentSession().remove(trainee);
    }
}