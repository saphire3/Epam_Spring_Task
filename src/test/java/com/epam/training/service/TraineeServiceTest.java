package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.exception.ConflictException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUser(user("John", "Doe"));
        trainee.setAddress("Yerevan");
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
    }

    @Test
    void create_success() {
        when(usernameGenerator.generate("John", "Doe")).thenReturn("john.doe");
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.empty());
        when(passwordGenerator.generate()).thenReturn("pass123");

        Trainee result = traineeService.create(trainee);

        assertEquals("john.doe", result.getUser().getUsername());
        assertEquals("pass123", result.getUser().getPassword());
        assertTrue(result.getUser().isActive());

        verify(traineeDao).save(trainee);
    }

    @Test
    void create_conflictWithTrainer() {
        when(usernameGenerator.generate("John", "Doe")).thenReturn("john.doe");
        when(trainerDao.findByUsername("john.doe")).thenReturn(Optional.of(new Trainer()));

        assertThrows(ConflictException.class, () -> traineeService.create(trainee));
    }

    @Test
    void update_updatesActiveField() {
        Trainee existing = new Trainee();
        existing.setUser(user("Old", "User"));

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        when(traineeDao.update(existing)).thenReturn(existing);

        Trainee updated = new Trainee();
        User updatedUser = user("New", "User");
        updatedUser.setActive(false);
        updated.setUser(updatedUser);

        updated.setAddress("New Address");
        updated.setDateOfBirth(LocalDate.of(2001, 1, 1));

        Trainee result = traineeService.update("john.doe", "pass", updated);

        assertFalse(result.getUser().isActive());
        assertEquals("New", result.getUser().getFirstName());
        assertEquals("New Address", result.getAddress());
    }

    @Test
    void findByUsername_success() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.findByUsername("john.doe", "pass");

        assertNotNull(result);
        verify(authService).requireTraineeAuth("john.doe", "pass");
    }

    @Test
    void findByUsername_notFound() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.findByUsername("john.doe", "pass"));
    }

    private User user(String first, String last) {
        User u = new User();
        u.setFirstName(first);
        u.setLastName(last);
        u.setActive(true);
        return u;
    }
}