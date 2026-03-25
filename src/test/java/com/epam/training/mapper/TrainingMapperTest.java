package com.epam.training.mapper;

import com.epam.training.dto.response.TraineeTrainingResponse;
import com.epam.training.dto.response.TrainerTrainingResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingMapperTest {

    @Test
    void toTraineeTrainingResponse_mapsCorrectly() {
        Training training = buildTraining();

        TraineeTrainingResponse response = TrainingMapper.toTraineeTrainingResponse(training);

        assertEquals("Morning Workout", response.getTrainingName());
        assertEquals(LocalDate.of(2026, 3, 20), response.getTrainingDate());
        assertEquals("FITNESS", response.getTrainingType());
        assertEquals(60, response.getDuration());
        assertEquals("Jane Smith", response.getTrainerName());
    }

    @Test
    void toTrainerTrainingResponse_mapsCorrectly() {
        Training training = buildTraining();

        TrainerTrainingResponse response = TrainingMapper.toTrainerTrainingResponse(training);

        assertEquals("Morning Workout", response.getTrainingName());
        assertEquals(LocalDate.of(2026, 3, 20), response.getTrainingDate());
        assertEquals("FITNESS", response.getTrainingType());
        assertEquals(60, response.getDuration());
        assertEquals("John Doe", response.getTraineeName());
    }

    private Training buildTraining() {
        User traineeUser = new User();
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");

        User trainerUser = new User();
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("FITNESS");

        Training training = new Training();
        training.setTrainingName("Morning Workout");
        training.setTrainingDate(LocalDate.of(2026, 3, 20));
        training.setDuration(60);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(type);
        return training;
    }
}