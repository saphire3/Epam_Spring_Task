package com.epam.training.service;

import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private AuthService authService;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerService service;

    @Test
    void shouldCreateTrainer() {
        User user = new User();
        user.setFirstName("Anna");
        user.setLastName("Brown");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("FITNESS");

        when(trainingTypeDao.findByName("FITNESS")).thenReturn(Optional.of(type));
        when(usernameGenerator.generate("Anna", "Brown")).thenReturn("anna.brown");
        when(passwordGenerator.generate()).thenReturn("password123");

        Trainer result = service.create(trainer, "FITNESS");

        assertEquals("anna.brown", result.getUser().getUsername());
        assertEquals("password123", result.getUser().getPassword());
        assertEquals("FITNESS", result.getSpecialization().getTrainingTypeName());
        verify(trainerDao).save(trainer);
    }

    @Test
    void shouldFindTrainerByUsernameAfterAuth() {
        User user = new User();
        user.setUsername("anna.brown");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));

        Trainer result = service.findByUsername("anna.brown", "trainer123");

        assertNotNull(result);
        verify(authService).requireTrainerAuth("anna.brown", "trainer123");
    }

    @Test
    void shouldUpdateTrainer() {
        User existingUser = new User();
        existingUser.setFirstName("Anna");
        existingUser.setLastName("Brown");
        existingUser.setUsername("anna.brown");

        Trainer existing = new Trainer();
        existing.setUser(existingUser);

        User updatedUser = new User();
        updatedUser.setFirstName("Ann");
        updatedUser.setLastName("Taylor");

        Trainer updated = new Trainer();
        updated.setUser(updatedUser);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("YOGA");

        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(existing));
        when(trainingTypeDao.findByName("YOGA")).thenReturn(Optional.of(type));
        when(trainerDao.update(existing)).thenReturn(existing);

        Trainer result = service.update("anna.brown", "trainer123", updated, "YOGA");

        assertEquals("Ann", result.getUser().getFirstName());
        assertEquals("Taylor", result.getUser().getLastName());
        assertEquals("YOGA", result.getSpecialization().getTrainingTypeName());
        verify(trainerDao).update(existing);
    }

    @Test
    void shouldChangePassword() {
        User user = new User();
        user.setUsername("anna.brown");
        user.setPassword("oldPass");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));

        service.changePassword("anna.brown", "oldPass", "newPass");

        assertEquals("newPass", trainer.getUser().getPassword());
        verify(trainerDao).update(trainer);
    }

    @Test
    void shouldDeactivateTrainer() {
        User user = new User();
        user.setUsername("anna.brown");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));

        service.deactivate("anna.brown", "trainer123");

        assertFalse(trainer.getUser().isActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    void shouldThrowWhenTrainerAlreadyInactive() {
        User user = new User();
        user.setUsername("anna.brown");
        user.setActive(false);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class,
                () -> service.deactivate("anna.brown", "trainer123"));
    }

    @Test
    void shouldActivateTrainer() {
        User user = new User();
        user.setUsername("anna.brown");
        user.setActive(false);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));

        service.activate("anna.brown", "trainer123");

        assertTrue(trainer.getUser().isActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    void shouldReturnAllTrainers() {
        when(trainerDao.findAll()).thenReturn(List.of(new Trainer(), new Trainer()));

        List<Trainer> result = service.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldThrowWhenTrainerNotFound() {
        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.empty());

        User updatedUser = new User();
        updatedUser.setFirstName("Ann");
        updatedUser.setLastName("Taylor");

        Trainer updated = new Trainer();
        updated.setUser(updatedUser);

        assertThrows(UserNotFoundException.class,
                () -> service.update("anna.brown", "trainer123", updated, "YOGA"));
    }
}