package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
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
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private AuthService authService;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeService service;

    @Test
    void shouldCreateTrainee() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Smith");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(usernameGenerator.generate("John", "Smith")).thenReturn("john.smith");
        when(passwordGenerator.generate()).thenReturn("randomPass");

        Trainee result = service.create(trainee);

        assertEquals("john.smith", result.getUser().getUsername());
        assertEquals("randomPass", result.getUser().getPassword());
        verify(traineeDao).save(trainee);
    }

    @Test
    void shouldFindTraineeByUsernameAfterAuth() {
        User user = new User();
        user.setUsername("john.smith");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        Trainee result = service.findByUsername("john.smith", "pass123");

        assertNotNull(result);
        verify(authService).requireTraineeAuth("john.smith", "pass123");
    }

    @Test
    void shouldUpdateTrainee() {
        User existingUser = new User();
        existingUser.setFirstName("John");
        existingUser.setLastName("Smith");
        existingUser.setUsername("john.smith");

        Trainee existing = new Trainee();
        existing.setUser(existingUser);
        existing.setAddress("Old");

        User updatedUser = new User();
        updatedUser.setFirstName("Johnny");
        updatedUser.setLastName("Johnson");

        Trainee updated = new Trainee();
        updated.setUser(updatedUser);
        updated.setAddress("New Address");

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(existing));
        when(traineeDao.update(existing)).thenReturn(existing);

        Trainee result = service.update("john.smith", "pass123", updated);

        assertEquals("Johnny", result.getUser().getFirstName());
        assertEquals("Johnson", result.getUser().getLastName());
        assertEquals("New Address", result.getAddress());
        verify(authService).requireTraineeAuth("john.smith", "pass123");
        verify(traineeDao).update(existing);
    }

    @Test
    void shouldChangePassword() {
        User user = new User();
        user.setUsername("john.smith");
        user.setPassword("oldPass");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        service.changePassword("john.smith", "oldPass", "newPass");

        assertEquals("newPass", trainee.getUser().getPassword());
        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldDeactivateTrainee() {
        User user = new User();
        user.setUsername("john.smith");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        service.deactivate("john.smith", "pass123");

        assertFalse(trainee.getUser().isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldThrowWhenDeactivatingInactiveTrainee() {
        User user = new User();
        user.setUsername("john.smith");
        user.setActive(false);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class,
                () -> service.deactivate("john.smith", "pass123"));
    }

    @Test
    void shouldActivateTrainee() {
        User user = new User();
        user.setUsername("john.smith");
        user.setActive(false);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        service.activate("john.smith", "pass123");

        assertTrue(trainee.getUser().isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldDeleteTraineeByUsername() {
        User user = new User();
        user.setUsername("john.smith");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        service.deleteByUsername("john.smith", "pass123");

        verify(traineeDao).delete(trainee);
    }

    @Test
    void shouldReturnUnassignedTrainers() {
        Trainer trainer = new Trainer();
        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(new Trainee()));
        when(trainerDao.findNotAssignedToTrainee("john.smith")).thenReturn(List.of(trainer));

        List<Trainer> result = service.getUnassignedTrainers("john.smith", "pass123");

        assertEquals(1, result.size());
        verify(authService).requireTraineeAuth("john.smith", "pass123");
    }

    @Test
    void shouldUpdateTrainerList() {
        User traineeUser = new User();
        traineeUser.setUsername("john.smith");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setUsername("anna.brown");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));

        service.updateTrainerList("john.smith", "pass123", List.of("anna.brown"));

        assertEquals(1, trainee.getTrainers().size());
        verify(traineeDao).update(trainee);
    }

    @Test
    void shouldThrowWhenTraineeNotFound() {
        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.empty());

        User updatedUser = new User();
        updatedUser.setFirstName("John");
        updatedUser.setLastName("Smith");

        Trainee updated = new Trainee();
        updated.setUser(updatedUser);

        assertThrows(UserNotFoundException.class,
                () -> service.update("john.smith", "pass123", updated));
    }
}