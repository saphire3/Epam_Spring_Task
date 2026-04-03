package com.epam.training.repository;

import com.epam.training.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUsername(String username);

    @Query("SELECT tr FROM Trainer tr WHERE tr NOT IN " +
           "(SELECT t FROM Trainee te JOIN te.trainers t WHERE te.username = :traineeUsername)")
    List<com.epam.training.model.Trainer> findTrainersNotAssignedToTrainee(@Param("traineeUsername") String traineeUsername);
}
