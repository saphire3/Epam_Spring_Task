package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.exception.AuthenticationException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.junit.jupiter.api.BeforeEach;
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

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUser(user("trainee.user", "pass123"));

        trainer = new Trainer();
        trainer.setUser(user("trainer.user", "pass456"));
    }

    @Test
    void authenticateTrainee_returnsTrue_whenCredentialsCorrect() {
        when(traineeDao.findByUsername("trainee.user")).thenReturn(Optional.of(trainee));

        assertTrue(authService.authenticateTrainee("trainee.user", "pass123"));
    }

    @Test
    void authenticateTrainee_returnsFalse_whenPasswordWrong() {
        when(traineeDao.findByUsername("trainee.user")).thenReturn(Optional.of(trainee));

        assertFalse(authService.authenticateTrainee("trainee.user", "wrong"));
    }

    @Test
    void authenticateTrainer_returnsTrue_whenCredentialsCorrect() {
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(trainer));

        assertTrue(authService.authenticateTrainer("trainer.user", "pass456"));
    }

    @Test
    void requireTraineeAuth_throws_whenInvalid() {
        when(traineeDao.findByUsername("trainee.user")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> authService.requireTraineeAuth("trainee.user", "pass123"));
    }

    @Test
    void requireTrainerAuth_throws_whenInvalid() {
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> authService.requireTrainerAuth("trainer.user", "pass456"));
    }

    @Test
    void requireAnyUserAuth_passes_whenTrainerValid() {
        when(traineeDao.findByUsername("trainer.user")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> authService.requireAnyUserAuth("trainer.user", "pass456"));
    }

    @Test
    void requireAnyUserAuth_throws_whenNoUserMatches() {
        when(traineeDao.findByUsername("ghost")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> authService.requireAnyUserAuth("ghost", "123"));
    }

    @Test
    void authenticateTrainee_throws_whenUsernameBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticateTrainee("", "pass123"));
    }

    @Test
    void authenticateTrainer_throws_whenPasswordBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.authenticateTrainer("trainer.user", ""));
    }

    private User user(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(true);
        return user;
    }
}