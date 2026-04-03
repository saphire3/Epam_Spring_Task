package com.epam.training.util;

import com.epam.training.model.Trainee;
import com.epam.training.repository.TraineeRepository;
import com.epam.training.repository.TrainerRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UsernameGeneratorTest {

    private final TraineeRepository traineeRepository = mock(TraineeRepository.class);
    private final TrainerRepository trainerRepository = mock(TrainerRepository.class);

    private final UsernameGenerator generator =
            new UsernameGenerator(traineeRepository, trainerRepository);

    @Test
    void shouldGenerateSimpleUsername() {
        when(traineeRepository.findAll()).thenReturn(List.of());
        when(trainerRepository.findAll()).thenReturn(List.of());

        String result = generator.generate("John", "Smith");

        assertEquals("john.smith", result);
    }

    @Test
    void shouldAddSuffixIfDuplicateExists() {
        Trainee existing = new Trainee();
        existing.setUsername("john.smith");

        when(traineeRepository.findAll()).thenReturn(List.of(existing));
        when(trainerRepository.findAll()).thenReturn(List.of());

        String result = generator.generate("John", "Smith");

        assertEquals("john.smith1", result);
    }

    @Test
    void shouldAddIncrementingSuffix() {
        Trainee e1 = new Trainee();
        e1.setUsername("john.smith");
        Trainee e2 = new Trainee();
        e2.setUsername("john.smith1");

        when(traineeRepository.findAll()).thenReturn(List.of(e1, e2));
        when(trainerRepository.findAll()).thenReturn(List.of());

        String result = generator.generate("John", "Smith");
        assertEquals("john.smith2", result);
    }
}
