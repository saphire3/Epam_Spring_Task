package com.epam.training.dao;

import com.epam.training.dto.TraineeTrainingFilter;
import com.epam.training.dto.TrainerTrainingFilter;
import com.epam.training.model.Training;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
        Session session = sessionFactory.getCurrentSession();

        StringBuilder hql = new StringBuilder("""
                select trn
                from Training trn
                join trn.trainee trainee
                join trainee.user tu
                join trn.trainer trainer
                join trainer.user trainerUser
                join trn.trainingType tt
                where tu.username = :username
                """);

        if (filter.getFromDate() != null) hql.append(" and trn.trainingDate >= :fromDate");
        if (filter.getToDate() != null) hql.append(" and trn.trainingDate <= :toDate");
        if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
            hql.append(" and lower(concat(trainerUser.firstName, ' ', trainerUser.lastName)) like :trainerName");
        }
        if (filter.getTrainingType() != null && !filter.getTrainingType().isBlank()) {
            hql.append(" and lower(tt.trainingTypeName) = :trainingType");
        }

        var query = session.createQuery(hql.toString(), Training.class)
                .setParameter("username", username);

        if (filter.getFromDate() != null) query.setParameter("fromDate", filter.getFromDate());
        if (filter.getToDate() != null) query.setParameter("toDate", filter.getToDate());
        if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
            query.setParameter("trainerName", "%" + filter.getTrainerName().toLowerCase() + "%");
        }
        if (filter.getTrainingType() != null && !filter.getTrainingType().isBlank()) {
            query.setParameter("trainingType", filter.getTrainingType().toLowerCase());
        }

        return query.getResultList();
    }

    public List<Training> findTrainerTrainings(String username, TrainerTrainingFilter filter) {
        Session session = sessionFactory.getCurrentSession();

        StringBuilder hql = new StringBuilder("""
                select trn
                from Training trn
                join trn.trainer trainer
                join trainer.user tu
                join trn.trainee trainee
                join trainee.user traineeUser
                where tu.username = :username
                """);

        if (filter.getFromDate() != null) hql.append(" and trn.trainingDate >= :fromDate");
        if (filter.getToDate() != null) hql.append(" and trn.trainingDate <= :toDate");
        if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
            hql.append(" and lower(concat(traineeUser.firstName, ' ', traineeUser.lastName)) like :traineeName");
        }

        var query = session.createQuery(hql.toString(), Training.class)
                .setParameter("username", username);

        if (filter.getFromDate() != null) query.setParameter("fromDate", filter.getFromDate());
        if (filter.getToDate() != null) query.setParameter("toDate", filter.getToDate());
        if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
            query.setParameter("traineeName", "%" + filter.getTraineeName().toLowerCase() + "%");
        }

        return query.getResultList();
    }
}