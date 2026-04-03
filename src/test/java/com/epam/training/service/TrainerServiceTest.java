package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.exception.ConflictException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.metrics.GymMetrics;
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

import java.util.List;
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
    @Mock
    private GymMetrics gymMetrics;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer trainer;
    private TrainingType yoga;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setUser(user("Jane", "Smith"));

        yoga = new TrainingType();
        yoga.setTrainingTypeName("YOGA");
    }

    @Test
    void create_setsGeneratedUsernamePasswordAndSaves() {
        when(trainingTypeDao.findByName("YOGA")).thenReturn(Optional.of(yoga));
        when(usernameGenerator.generate("Jane", "Smith")).thenReturn("Jane.Smith");
        when(traineeDao.findByUsername("Jane.Smith")).thenReturn(Optional.empty());
        when(passwordGenerator.generate()).thenReturn("generatedPass");

        Trainer result = trainerService.create(trainer, "YOGA");

        verify(trainerDao).save(trainer);
        assertSame(trainer, result);
        assertEquals("Jane.Smith", trainer.getUser().getUsername());
        assertEquals("generatedPass", trainer.getUser().getPassword());
        assertTrue(trainer.getUser().isActive());
        assertEquals(yoga, trainer.getSpecialization());
    }

    @Test
    void create_throwsWhenTrainerNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.create(null, "YOGA"));
    }

    @Test
    void create_throwsWhenSpecializationBlank() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.create(trainer, " "));
    }

    @Test
    void create_throwsWhenSpecializationUnknown() {
        when(trainingTypeDao.findByName("YOGA")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trainerService.create(trainer, "YOGA"));
    }

    @Test
    void create_throwsConflictWhenUsernameAlreadyExistsAsTrainee() {
        when(trainingTypeDao.findByName("YOGA")).thenReturn(Optional.of(yoga));
        when(usernameGenerator.generate("Jane", "Smith")).thenReturn("Jane.Smith");
        when(traineeDao.findByUsername("Jane.Smith")).thenReturn(Optional.of(new Trainee()));

        assertThrows(ConflictException.class, () -> trainerService.create(trainer, "YOGA"));
    }

    @Test
    void findByUsername_returnsTrainer() {
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findByUsername("trainer.user", "pass");

        assertSame(trainer, result);
        verify(authService).requireTrainerAuth("trainer.user", "pass");
    }

    @Test
    void findByUsername_throwsWhenMissing() {
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.findByUsername("trainer.user", "pass"));
    }

    @Test
    void update_updatesNamesAndActive() {
        Trainer existing = new Trainer();
        existing.setUser(user("Old", "Name"));

        Trainer updated = new Trainer();
        User updatedUser = user("New", "Name");
        updatedUser.setActive(false);
        updated.setUser(updatedUser);

        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(existing));
        when(trainerDao.update(existing)).thenReturn(existing);

        Trainer result = trainerService.update("trainer.user", "pass", updated, null);

        assertEquals("New", result.getUser().getFirstName());
        assertEquals("Name", result.getUser().getLastName());
        assertFalse(result.getUser().isActive());
    }

    @Test
    void update_updatesSpecializationWhenProvided() {
        Trainer existing = new Trainer();
        existing.setUser(user("Old", "Name"));

        Trainer updated = new Trainer();
        updated.setUser(user("New", "Name"));

        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(existing));
        when(trainingTypeDao.findByName("YOGA")).thenReturn(Optional.of(yoga));
        when(trainerDao.update(existing)).thenReturn(existing);

        Trainer result = trainerService.update("trainer.user", "pass", updated, "YOGA");

        assertEquals(yoga, result.getSpecialization());
    }

    @Test
    void changePassword_updatesPassword() {
        trainer.getUser().setUsername("trainer.user");
        trainer.getUser().setPassword("oldPass");

        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(trainer));

        trainerService.changePassword("trainer.user", "oldPass", "newPass");

        assertEquals("newPass", trainer.getUser().getPassword());
        verify(trainerDao).update(trainer);
    }

    @Test
    void changePassword_throwsWhenNewPasswordBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.changePassword("trainer.user", "oldPass", " "));
    }

    @Test
    void changePassword_throwsWhenSamePassword() {
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.changePassword("trainer.user", "same", "same"));
    }

    @Test
    void activate_setsTrue() {
        trainer.getUser().setActive(false);
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(trainer));

        trainerService.activate("trainer.user", "pass");

        assertTrue(trainer.getUser().isActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    void activate_throwsWhenAlreadyActive() {
        trainer.getUser().setActive(true);
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class,
                () -> trainerService.activate("trainer.user", "pass"));
    }

    @Test
    void deactivate_setsFalse() {
        trainer.getUser().setActive(true);
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(trainer));

        trainerService.deactivate("trainer.user", "pass");

        assertFalse(trainer.getUser().isActive());
        verify(trainerDao).update(trainer);
    }

    @Test
    void deactivate_throwsWhenAlreadyInactive() {
        trainer.getUser().setActive(false);
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(trainer));

        assertThrows(IllegalStateException.class,
                () -> trainerService.deactivate("trainer.user", "pass"));
    }

    @Test
    void getByUsername_delegatesToFindByUsername() {
        when(trainerDao.findByUsername("trainer.user")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getByUsername("trainer.user", "pass");

        assertSame(trainer, result);
    }

    @Test
    void findAll_returnsAll() {
        when(trainerDao.findAll()).thenReturn(List.of(trainer));

        List<Trainer> result = trainerService.findAll();

        assertEquals(1, result.size());
    }

    private User user(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(true);
        return user;
    }
}