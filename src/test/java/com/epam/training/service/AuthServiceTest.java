package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldAuthenticateTrainee() {
        User user = new User();
        user.setUsername("john.smith");
        user.setPassword("pass123");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        assertTrue(authService.authenticateTrainee("john.smith", "pass123"));
    }

    @Test
    void shouldNotAuthenticateTraineeWithWrongPassword() {
        User user = new User();
        user.setUsername("john.smith");
        user.setPassword("pass123");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        assertFalse(authService.authenticateTrainee("john.smith", "wrong"));
    }

    @Test
    void shouldAuthenticateTrainer() {
        User user = new User();
        user.setUsername("anna.brown");
        user.setPassword("trainer123");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));

        assertTrue(authService.authenticateTrainer("anna.brown", "trainer123"));
    }

    @Test
    void shouldRequireTraineeAuth() {
        User user = new User();
        user.setUsername("john.smith");
        user.setPassword("pass123");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> authService.requireTraineeAuth("john.smith", "pass123"));
    }

    @Test
    void shouldThrowWhenTraineeAuthFails() {
        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.requireTraineeAuth("john.smith", "pass123"));
    }

    @Test
    void shouldThrowWhenTrainerAuthFails() {
        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.requireTrainerAuth("anna.brown", "trainer123"));
    }
}