package com.epam.training.util;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UsernameGeneratorTest {

    private final TraineeDao traineeDao = mock(TraineeDao.class);
    private final TrainerDao trainerDao = mock(TrainerDao.class);

    private final UsernameGenerator generator =
            new UsernameGenerator(traineeDao, trainerDao);

    @Test
    void shouldGenerateSimpleUsername() {
        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("john.smith")).thenReturn(Optional.empty());

        String result = generator.generate("John", "Smith");

        assertEquals("john.smith", result);
    }

    @Test
    void shouldAddSuffixIfDuplicateExistsInTrainees() {
        User user = new User();
        user.setUsername("john.smith");

        Trainee existing = new Trainee();
        existing.setUser(user);

        when(traineeDao.findByUsername("john.smith")).thenReturn(Optional.of(existing));
        when(traineeDao.findByUsername("john.smith1")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("john.smith")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("john.smith1")).thenReturn(Optional.empty());

        String result = generator.generate("John", "Smith");

        assertEquals("john.smith1", result);
    }

    @Test
    void shouldAddSuffixIfDuplicateExistsInTrainers() {
        User user = new User();
        user.setUsername("anna.brown");

        Trainer existing = new Trainer();
        existing.setUser(user);

        when(traineeDao.findByUsername("anna.brown")).thenReturn(Optional.empty());
        when(traineeDao.findByUsername("anna.brown1")).thenReturn(Optional.empty());
        when(trainerDao.findByUsername("anna.brown")).thenReturn(Optional.of(existing));
        when(trainerDao.findByUsername("anna.brown1")).thenReturn(Optional.empty());

        String result = generator.generate("Anna", "Brown");

        assertEquals("anna.brown1", result);
    }
}