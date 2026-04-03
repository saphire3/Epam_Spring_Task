package com.epam.training.service;

import com.epam.training.exception.TrainingTypeNotFoundException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.repository.TraineeRepository;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingRepository;
import com.epam.training.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    void shouldCreateTraining() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");
        Trainer trainer = new Trainer();
        trainer.setUsername("anna.brown");
        TrainingType type = new TrainingType("CARDIO");

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("CARDIO")).thenReturn(Optional.of(type));

        Training savedTraining = new Training();
        when(trainingRepository.save(any(Training.class))).thenReturn(savedTraining);

        Training result = trainingService.create(
                "john.smith", "anna.brown", "CARDIO", "Morning Cardio",
                LocalDate.now(), 60);

        assertNotNull(result);
        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void shouldThrowWhenTraineeNotFound() {
        when(traineeRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainingService.create("unknown", "anna.brown", "CARDIO", "Test", LocalDate.now(), 60));
    }

    @Test
    void shouldThrowWhenTrainerNotFound() {
        Trainee trainee = new Trainee();
        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainingService.create("john.smith", "unknown", "CARDIO", "Test", LocalDate.now(), 60));
    }

    @Test
    void shouldThrowWhenTrainingTypeNotFound() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class,
                () -> trainingService.create("john.smith", "anna.brown", "UNKNOWN", "Test", LocalDate.now(), 60));
    }

    @Test
    void shouldGetTraineeTrainings() {
        List<Training> trainings = List.of(new Training());
        when(trainingRepository.findTraineeTrainings("john.smith", null, null, null, null))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings("john.smith", null, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    void shouldGetTrainerTrainings() {
        List<Training> trainings = List.of(new Training());
        when(trainingRepository.findTrainerTrainings("anna.brown", null, null, null))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings("anna.brown", null, null, null);

        assertEquals(1, result.size());
    }
}
