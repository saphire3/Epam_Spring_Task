package com.epam.training.dao;

import com.epam.training.model.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeDao {

    private final SessionFactory sessionFactory;

    public TrainingTypeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<TrainingType> findByName(String name) {
        return sessionFactory.getCurrentSession()
                .createQuery("from TrainingType tt where tt.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }

    public List<TrainingType> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from TrainingType", TrainingType.class)
                .getResultList();
    }

    public void save(TrainingType type) {
        sessionFactory.getCurrentSession().persist(type);
    }
}