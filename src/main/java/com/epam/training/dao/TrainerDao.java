package com.epam.training.dao;

import com.epam.training.model.Trainer;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDao {

    private final SessionFactory sessionFactory;

    public TrainerDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Trainer trainer) {
        sessionFactory.getCurrentSession().persist(trainer);
    }

    public Trainer update(Trainer trainer) {
        return (Trainer) sessionFactory.getCurrentSession().merge(trainer);
    }

    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Trainer.class, id));
    }

    public Optional<Trainer> findByUsername(String username) {
        return sessionFactory.getCurrentSession()
                .createQuery("""
                        select t
                        from Trainer t
                        join t.user u
                        where u.username = :username
                        """, Trainer.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public List<Trainer> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Trainer", Trainer.class)
                .getResultList();
    }

    public List<Trainer> findNotAssignedToTrainee(String traineeUsername) {
        return sessionFactory.getCurrentSession()
                .createQuery("""
                        select tr
                        from Trainer tr
                        where tr.id not in (
                            select t2.id
                            from Trainee t
                            join t.trainers t2
                            join t.user tu
                            where tu.username = :username
                        )
                        """, Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    public void delete(Trainer trainer) {
        sessionFactory.getCurrentSession().remove(trainer);
    }
}