package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.dto.filter.TraineeTrainingFilter;
import com.epam.training.dto.filter.TrainerTrainingFilter;
import com.epam.training.exception.AuthenticationException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private TrainingService trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType type;
    private Training training;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUser(user("john.doe", "pass123"));

        trainer = new Trainer();
        trainer.setUser(user("jane.smith", "pass456"));

        type = new TrainingType();
        type.setTrainingTypeName("FITNESS");

        training = new Training();
        training.setTrainingName("Morning Workout");
        training.setTrainingDate(LocalDate.of(2026, 3, 20));
        training.setDuration(60);
    }

    @Test
    void create_savesTraining_whenValid() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("FITNESS")).thenReturn(Optional.of(type));

        Training result = trainingService.create("john.doe", "pass123", "jane.smith", "FITNESS", training);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao).save(captor.capture());

        assertSame(training, result);
        assertEquals(trainee, captor.getValue().getTrainee());
        assertEquals(trainer, captor.getValue().getTrainer());
        assertEquals(type, captor.getValue().getTrainingType());
        verify(authService).requireTraineeAuth("john.doe", "pass123");
    }

    @Test
    void create_throws_whenTrainingNull() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create("john.doe", "pass123", "jane.smith", "FITNESS", null));
    }

    @Test
    void create_throws_whenTrainingNameBlank() {
        training.setTrainingName(" ");

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create("john.doe", "pass123", "jane.smith", "FITNESS", training));
    }

    @Test
    void create_throws_whenTrainingDateNull() {
        training.setTrainingDate(null);

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create("john.doe", "pass123", "jane.smith", "FITNESS", training));
    }

    @Test
    void create_throws_whenDurationInvalid() {
        training.setDuration(0);

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create("john.doe", "pass123", "jane.smith", "FITNESS", training));
    }

    @Test
    void create_throws_whenTrainingTypeBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create("john.doe", "pass123", "jane.smith", " ", training));
    }

    @Test
    void create_throws_whenTraineeNotFound() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainingService.create("john.doe", "pass123", "jane.smith", "FITNESS", training));
    }

    @Test
    void create_throws_whenTrainerNotFound() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("jane.smith")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainingService.create("john.doe", "pass123", "jane.smith", "FITNESS", training));
    }

    @Test
    void create_throws_whenTypeMissing() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeDao.findByName("FITNESS")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainingService.create("john.doe", "pass123", "jane.smith", "FITNESS", training));
    }

    @Test
    void getTraineeTrainings_usesSafeFilter_whenNull() {
        when(trainingDao.findTraineeTrainings(eq("john.doe"), any(TraineeTrainingFilter.class)))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTraineeTrainings("john.doe", "pass123", null);

        assertEquals(1, result.size());
        verify(authService).requireTraineeAuth("john.doe", "pass123");
    }

    @Test
    void getTrainerTrainings_usesSafeFilter_whenNull() {
        when(trainingDao.findTrainerTrainings(eq("jane.smith"), any(TrainerTrainingFilter.class)))
                .thenReturn(List.of(training));

        List<Training> result = trainingService.getTrainerTrainings("jane.smith", "pass456", null);

        assertEquals(1, result.size());
        verify(authService).requireTrainerAuth("jane.smith", "pass456");
    }

    @Test
    void getTraineeTrainings_throws_whenUsernameBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.getTraineeTrainings("", "pass123", new TraineeTrainingFilter()));
    }

    @Test
    void findAll_returnsAll() {
        when(trainingDao.findAll()).thenReturn(List.of(training));

        List<Training> result = trainingService.findAll();

        assertEquals(1, result.size());
    }

    private User user(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setActive(true);
        return user;
    }
}