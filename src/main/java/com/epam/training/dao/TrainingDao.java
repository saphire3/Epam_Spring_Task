package com.epam.training.dao;

import com.epam.training.dto.filter.TraineeTrainingFilter;
import com.epam.training.dto.filter.TrainerTrainingFilter;
import com.epam.training.model.Training;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingDao {

    private final SessionFactory sessionFactory;

    public TrainingDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Training training) {
        sessionFactory.getCurrentSession().persist(training);
    }

    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Training.class, id));
    }

    public List<Training> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Training", Training.class)
                .getResultList();
    }

    public List<Training> findTraineeTrainings(String username, TraineeTrainingFilter filter) {
        StringBuilder hql = new StringBuilder("""
                select t
                from Training t
                where t.trainee.user.username = :username
                """);

        if (filter.getPeriodFrom() != null) {
            hql.append(" and t.trainingDate >= :periodFrom");
        }
        if (filter.getPeriodTo() != null) {
            hql.append(" and t.trainingDate <= :periodTo");
        }
        if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
            hql.append("""
                     and (
                        lower(t.trainer.user.firstName) like lower(:trainerName)
                        or lower(t.trainer.user.lastName) like lower(:trainerName)
                     )
                    """);
        }
        if (filter.getTrainingTypeName() != null && !filter.getTrainingTypeName().isBlank()) {
            hql.append(" and lower(t.trainingType.trainingTypeName) = lower(:trainingTypeName)");
        }

        Query<Training> query = sessionFactory.getCurrentSession()
                .createQuery(hql.toString(), Training.class)
                .setParameter("username", username);

        if (filter.getPeriodFrom() != null) {
            query.setParameter("periodFrom", filter.getPeriodFrom());
        }
        if (filter.getPeriodTo() != null) {
            query.setParameter("periodTo", filter.getPeriodTo());
        }
        if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
            query.setParameter("trainerName", "%" + filter.getTrainerName() + "%");
        }
        if (filter.getTrainingTypeName() != null && !filter.getTrainingTypeName().isBlank()) {
            query.setParameter("trainingTypeName", filter.getTrainingTypeName());
        }

        return query.getResultList();
    }

    public List<Training> findTrainerTrainings(String username, TrainerTrainingFilter filter) {
        StringBuilder hql = new StringBuilder("""
                select t
                from Training t
                where t.trainer.user.username = :username
                """);

        if (filter.getPeriodFrom() != null) {
            hql.append(" and t.trainingDate >= :periodFrom");
        }
        if (filter.getPeriodTo() != null) {
            hql.append(" and t.trainingDate <= :periodTo");
        }
        if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
            hql.append("""
                     and (
                        lower(t.trainee.user.firstName) like lower(:traineeName)
                        or lower(t.trainee.user.lastName) like lower(:traineeName)
                     )
                    """);
        }

        Query<Training> query = sessionFactory.getCurrentSession()
                .createQuery(hql.toString(), Training.class)
                .setParameter("username", username);

        if (filter.getPeriodFrom() != null) {
            query.setParameter("periodFrom", filter.getPeriodFrom());
        }
        if (filter.getPeriodTo() != null) {
            query.setParameter("periodTo", filter.getPeriodTo());
        }
        if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
            query.setParameter("traineeName", "%" + filter.getTraineeName() + "%");
        }

        return query.getResultList();
    }
}