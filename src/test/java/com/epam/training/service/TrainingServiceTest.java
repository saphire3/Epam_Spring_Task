package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.dto.TraineeTrainingFilter;
import com.epam.training.dto.TrainerTrainingFilter;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private AuthService authService;

    @InjectMocks
    private TrainingService service;

    @Test
    void shouldCreateTraining() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("CARDIO");

        Training training = new Training();
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("CARDIO")).thenReturn(Optional.of(type));

        Training result = service.create(
                "john.smith",
                "pass123",
                "anna.brown",
                "CARDIO",
                training
        );

        assertEquals(trainee, result.getTrainee());
        assertEquals(trainer, result.getTrainer());
        assertEquals(type, result.getTrainingType());
        verify(authService).requireTraineeAuth("john.smith", "pass123");
        verify(trainingDao).save(training);
    }

    @Test
    void shouldThrowWhenTrainingNameBlank() {
        Training training = new Training();
        training.setTrainingName(" ");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);

        assertThrows(IllegalArgumentException.class,
                () -> service.create("john.smith", "pass123", "anna.brown", "CARDIO", training));
    }

    @Test
    void shouldThrowWhenTraineeNotFound() {
        Training training = new Training();
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.create("john.smith", "pass123", "anna.brown", "CARDIO", training));
    }

    @Test
    void shouldReturnTraineeTrainings() {
        when(trainingDao.findTraineeTrainings(eq("john.smith"), any(TraineeTrainingFilter.class)))
                .thenReturn(List.of(new Training(), new Training()));

        List<Training> result = service.getTraineeTrainings("john.smith", "pass123", new TraineeTrainingFilter());

        assertEquals(2, result.size());
        verify(authService).requireTraineeAuth("john.smith", "pass123");
    }

    @Test
    void shouldReturnTrainerTrainings() {
        when(trainingDao.findTrainerTrainings(eq("anna.brown"), any(TrainerTrainingFilter.class)))
                .thenReturn(List.of(new Training()));

        List<Training> result = service.getTrainerTrainings("anna.brown", "trainer123", new TrainerTrainingFilter());

        assertEquals(1, result.size());
        verify(authService).requireTrainerAuth("anna.brown", "trainer123");
    }

    @Test
    void shouldReturnAllTrainings() {
        when(trainingDao.findAll()).thenReturn(List.of(new Training(), new Training(), new Training()));

        List<Training> result = service.findAll();

        assertEquals(3, result.size());
    }
}