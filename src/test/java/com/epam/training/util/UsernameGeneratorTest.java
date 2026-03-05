package com.epam.training.util;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainee;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UsernameGeneratorTest {

    private final TraineeDao traineeDao = mock(TraineeDao.class);
    private final TrainerDao trainerDao = mock(TrainerDao.class);

    private final UsernameGenerator generator =
            new UsernameGenerator(traineeDao, trainerDao);

    @Test
    void shouldGenerateSimpleUsername() {

        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());

        String result = generator.generate("John", "Smith");

        assertEquals("john.smith", result);
    }

    @Test
    void shouldAddSuffixIfDuplicateExists() {

        Trainee existing = new Trainee();
        existing.setUsername("john.smith");

        when(traineeDao.findAll()).thenReturn(List.of(existing));
        when(trainerDao.findAll()).thenReturn(List.of());

        String result = generator.generate("John", "Smith");

        assertEquals("john.smith1", result);
    }
}