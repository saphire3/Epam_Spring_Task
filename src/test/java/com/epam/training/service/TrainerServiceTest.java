package com.epam.training.service;

import com.epam.training.exception.TrainingTypeNotFoundException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingTypeRepository;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    void shouldCreateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Anna");
        trainer.setLastName("Brown");

        TrainingType type = new TrainingType("FITNESS");
        when(trainingTypeRepository.findByTrainingTypeName("FITNESS")).thenReturn(Optional.of(type));
        when(usernameGenerator.generate("Anna", "Brown")).thenReturn("anna.brown");
        when(passwordGenerator.generate()).thenReturn("rawpass123");
        when(passwordEncoder.encode("rawpass123")).thenReturn("$2a$10$encoded");
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        String[] result = trainerService.create(trainer, "FITNESS");

        assertEquals("anna.brown", result[0]);
        assertEquals("rawpass123", result[1]);
        assertEquals("anna.brown", trainer.getUsername());
        assertEquals("$2a$10$encoded", trainer.getPassword());
        assertTrue(trainer.isActive());
        assertEquals(type, trainer.getSpecialization());
    }

    @Test
    void shouldThrowWhenTrainingTypeNotFound() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Anna");
        trainer.setLastName("Brown");

        when(trainingTypeRepository.findByTrainingTypeName("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class,
                () -> trainerService.create(trainer, "UNKNOWN"));
    }

    @Test
    void shouldFindByUsername() {
        Trainer trainer = new Trainer();
        trainer.setUsername("anna.brown");

        when(trainerRepository.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.findByUsername("anna.brown");
        assertEquals("anna.brown", result.getUsername());
    }

    @Test
    void shouldThrowWhenTrainerNotFound() {
        when(trainerRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> trainerService.findByUsername("unknown"));
    }

    @Test
    void shouldUpdateTrainer() {
        Trainer existing = new Trainer();
        existing.setUsername("anna.brown");
        existing.setFirstName("Anna");
        existing.setLastName("Brown");

        Trainer updated = new Trainer();
        updated.setFirstName("Anne");
        updated.setLastName("Brown");

        when(trainerRepository.findByUsername("anna.brown")).thenReturn(Optional.of(existing));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existing);

        Trainer result = trainerService.update("anna.brown", updated, null);

        assertEquals("Anne", result.getFirstName());
    }

    @Test
    void shouldUpdateTrainerWithSpecialization() {
        Trainer existing = new Trainer();
        existing.setUsername("anna.brown");

        Trainer updated = new Trainer();
        updated.setFirstName("Anna");
        updated.setLastName("Brown");

        TrainingType yoga = new TrainingType("YOGA");
        when(trainerRepository.findByUsername("anna.brown")).thenReturn(Optional.of(existing));
        when(trainingTypeRepository.findByTrainingTypeName("YOGA")).thenReturn(Optional.of(yoga));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existing);

        trainerService.update("anna.brown", updated, "YOGA");

        assertEquals(yoga, existing.getSpecialization());
    }

    @Test
    void shouldActivateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("anna.brown");
        trainer.setActive(false);

        when(trainerRepository.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        trainerService.activate("anna.brown");

        assertTrue(trainer.isActive());
    }

    @Test
    void shouldDeactivateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("anna.brown");
        trainer.setActive(true);

        when(trainerRepository.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        trainerService.deactivate("anna.brown");

        assertFalse(trainer.isActive());
    }

    @Test
    void shouldChangePassword() {
        Trainer trainer = new Trainer();
        trainer.setUsername("anna.brown");

        when(trainerRepository.findByUsername("anna.brown")).thenReturn(Optional.of(trainer));
        when(passwordEncoder.encode("newpass")).thenReturn("$2a$10$newencoded");
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        trainerService.changePassword("anna.brown", "newpass");

        assertEquals("$2a$10$newencoded", trainer.getPassword());
    }

    @Test
    void shouldFindAll() {
        when(trainerRepository.findAll()).thenReturn(List.of(new Trainer()));

        List<Trainer> result = trainerService.findAll();

        assertEquals(1, result.size());
    }
}
