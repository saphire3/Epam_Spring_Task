package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.exception.ConflictException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private AuthService authService;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer trainer;
    private TrainingType type;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setUser(user("Jane", "Smith"));

        type = new TrainingType();
        type.setTrainingTypeName("YOGA");
    }

    @Test
    void create_success() {
        when(trainingTypeDao.findByName("YOGA")).thenReturn(Optional.of(type));
        when(usernameGenerator.generate("Jane", "Smith")).thenReturn("jane.smith");
        when(traineeDao.findByUsername("jane.smith")).thenReturn(Optional.empty());
        when(passwordGenerator.generate()).thenReturn("pass123");

        Trainer result = trainerService.create(trainer, "YOGA");

        assertEquals("jane.smith", result.getUser().getUsername());
        assertTrue(result.getUser().isActive());
        assertEquals(type, result.getSpecialization());

        verify(trainerDao).save(trainer);
    }

    @Test
    void create_conflictWithTrainee() {
        when(trainingTypeDao.findByName("YOGA")).thenReturn(Optional.of(type));
        when(usernameGenerator.generate("Jane", "Smith")).thenReturn("jane.smith");
        when(traineeDao.findByUsername("jane.smith")).thenReturn(Optional.of(new Trainee()));

        assertThrows(ConflictException.class,
                () -> trainerService.create(trainer, "YOGA"));
    }

    @Test
    void update_updatesActiveField() {
        Trainer existing = new Trainer();
        existing.setUser(user("Old", "User"));

        when(trainerDao.findByUsername("jane.smith")).thenReturn(Optional.of(existing));
        when(trainerDao.update(existing)).thenReturn(existing);

        Trainer updated = new Trainer();
        User updatedUser = user("New", "User");
        updatedUser.setActive(false);
        updated.setUser(updatedUser);

        Trainer result = trainerService.update("jane.smith", "pass", updated, null);

        assertFalse(result.getUser().isActive());
        assertEquals("New", result.getUser().getFirstName());
    }

    @Test
    void findByUsername_success() {
        when(trainerDao.findByUsername("jane.smith")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findByUsername("jane.smith", "pass");

        assertNotNull(result);
        verify(authService).requireTrainerAuth("jane.smith", "pass");
    }

    @Test
    void findByUsername_notFound() {
        when(trainerDao.findByUsername("jane.smith")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.findByUsername("jane.smith", "pass"));
    }

    private User user(String first, String last) {
        User u = new User();
        u.setFirstName(first);
        u.setLastName(last);
        u.setActive(true);
        return u;
    }
}