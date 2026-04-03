package com.epam.training.repository;

import com.epam.training.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("SELECT t FROM Training t WHERE t.trainee.username = :username " +
           "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
           "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
           "AND (:trainerName IS NULL OR t.trainer.username = :trainerName) " +
           "AND (:trainingType IS NULL OR t.trainingType.trainingTypeName = :trainingType)")
    List<Training> findTraineeTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingType") String trainingType);

    @Query("SELECT t FROM Training t WHERE t.trainer.username = :username " +
           "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
           "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
           "AND (:traineeName IS NULL OR t.trainee.username = :traineeName)")
    List<Training> findTrainerTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName);

    void deleteByTraineeUsername(String traineeUsername);
}
