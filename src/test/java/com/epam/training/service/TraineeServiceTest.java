package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
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

import java.time.LocalDate;
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
    private TraineeService traineeService;

    private Trainee trainee;
    private Trainer trainer1;
    private Trainer trainer2;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUser(user("John", "Doe"));
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("Yerevan");

        trainer1 = trainer("jane.smith", "Jane", "Smith", "YOGA");
        trainer2 = trainer("mike.brown", "Mike", "Brown", "FITNESS");
    }

    @Test
    void create_setsGeneratedUsernamePasswordAndSaves() {
        when(usernameGenerator.generate("John", "Doe")).thenReturn("John.Doe");
        when(trainerDao.findByUsername("John.Doe")).thenReturn(Optional.empty());
        when(passwordGenerator.generate()).thenReturn("generatedPass");

        Trainee result = traineeService.create(trainee);

        verify(traineeDao).save(trainee);
        assertSame(trainee, result);
        assertEquals("John.Doe", trainee.getUser().getUsername());
        assertEquals("generatedPass", trainee.getUser().getPassword());
        assertTrue(trainee.getUser().isActive());
    }

    @Test
    void create_throwsWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.create(null));
    }

    @Test
    void create_throwsConflictWhenUsernameAlreadyExistsAsTrainer() {
        when(usernameGenerator.generate("John", "Doe")).thenReturn("John.Doe");
        when(trainerDao.findByUsername("John.Doe")).thenReturn(Optional.of(new Trainer()));

        assertThrows(ConflictException.class, () -> traineeService.create(trainee));
    }

    @Test
    void findByUsername_returnsTrainee() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.findByUsername("john.doe", "pass");

        assertSame(trainee, result);
        verify(authService).requireTraineeAuth("john.doe", "pass");
    }

    @Test
    void findByUsername_throwsWhenMissing() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.findByUsername("john.doe", "pass"));
    }

    @Test
    void update_updatesFieldsAndActive() {
        Trainee existing = new Trainee();
        existing.setUser(user("Old", "Name"));
        existing.setAddress("Old");
        existing.setDateOfBirth(LocalDate.of(1990, 1, 1));

        Trainee updated = new Trainee();
        User updatedUser = user("New", "Name");
        updatedUser.setActive(false);
        updated.setUser(updatedUser);
        updated.setAddress("New Address");
        updated.setDateOfBirth(LocalDate.of(2001, 2, 2));

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        when(traineeDao.update(existing)).thenReturn(existing);

        Trainee result = traineeService.update("john.doe", "pass", updated);

        assertEquals("New", result.getUser().getFirstName());
        assertEquals("Name", result.getUser().getLastName());
        assertFalse(result.getUser().isActive());
        assertEquals("New Address", result.getAddress());
        assertEquals(LocalDate.of(2001, 2, 2), result.getDateOfBirth());
    }

    @Test
    void changePassword_updatesPassword() {
        trainee.getUser().setUsername("john.doe");
        trainee.getUser().setPassword("oldPass");

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.changePassword("john.doe", "oldPass", "newPass");

        assertEquals("newPass", trainee.getUser().getPassword());
        verify(traineeDao).update(trainee);
    }

    @Test
    void changePassword_throwsWhenBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.changePassword("john.doe", "oldPass", " "));
    }

    @Test
    void changePassword_throwsWhenSame() {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.changePassword("john.doe", "same", "same"));
    }

    @Test
    void activate_setsTrue() {
        trainee.getUser().setActive(false);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.activate("john.doe", "pass");

        assertTrue(trainee.getUser().isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void activate_throwsWhenAlreadyActive() {
        trainee.getUser().setActive(true);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class,
                () -> traineeService.activate("john.doe", "pass"));
    }

    @Test
    void deactivate_setsFalse() {
        trainee.getUser().setActive(true);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.deactivate("john.doe", "pass");

        assertFalse(trainee.getUser().isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void deactivate_throwsWhenAlreadyInactive() {
        trainee.getUser().setActive(false);
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class,
                () -> traineeService.deactivate("john.doe", "pass"));
    }

    @Test
    void deleteByUsername_deletes() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));

        traineeService.deleteByUsername("john.doe", "pass");

        verify(traineeDao).delete(trainee);
    }

    @Test
    void getUnassignedTrainers_returnsList() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findNotAssignedToTrainee("john.doe")).thenReturn(List.of(trainer1, trainer2));

        List<Trainer> result = traineeService.getUnassignedTrainers("john.doe", "pass");

        assertEquals(2, result.size());
    }

    @Test
    void getUnassignedTrainers_throwsWhenTraineeMissing() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.getUnassignedTrainers("john.doe", "pass"));
    }

    @Test
    void updateTrainerList_replacesTrainers() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("jane.smith")).thenReturn(Optional.of(trainer1));
        when(trainerDao.findByUsername("mike.brown")).thenReturn(Optional.of(trainer2));

        List<Trainer> result = traineeService.updateTrainerList(
                "john.doe", "pass", List.of("jane.smith", "mike.brown")
        );

        assertEquals(2, result.size());
        assertEquals(2, trainee.getTrainers().size());
        verify(traineeDao).update(trainee);
    }

    @Test
    void updateTrainerList_throwsWhenListNull() {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTrainerList("john.doe", "pass", null));
    }

    @Test
    void updateTrainerList_throwsWhenTrainerMissing() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.updateTrainerList("john.doe", "pass", List.of("missing")));
    }

    private User user(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(true);
        return user;
    }

    private Trainer trainer(String username, String firstName, String lastName, String specializationName) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(true);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName(specializationName);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);
        return trainer;
    }
}