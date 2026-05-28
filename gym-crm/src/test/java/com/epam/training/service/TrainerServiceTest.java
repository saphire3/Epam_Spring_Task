package com.epam.training.service;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainer;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private UsernameGenerator usernameGenerator;
    @Mock private PasswordGenerator passwordGenerator;
    @InjectMocks private TrainerService service;

    @Test
    void shouldCreateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Anna");
        trainer.setLastName("Brown");

        when(usernameGenerator.generate(any(), any())).thenReturn("anna.brown");
        when(passwordGenerator.generate()).thenReturn("password123");
        when(trainerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainer result = service.create(trainer);

        assertEquals("anna.brown", result.getUsername());
        verify(trainerRepository).save(any());
    }

    @Test
    void shouldUpdateTrainer() {
        Trainer existing = new Trainer();
        existing.setId(1L);
        existing.setFirstName("Old");
        existing.setLastName("Name");

        Trainer updated = new Trainer();
        updated.setFirstName("New");
        updated.setLastName("Name");

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(trainerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainer result = service.update(1L, updated);

        assertEquals("New", result.getFirstName());
        verify(trainerRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTrainerNotFound() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());
        Trainer updated = new Trainer();
        updated.setFirstName("New");
        updated.setLastName("Name");
        assertThrows(UserNotFoundException.class, () -> service.update(1L, updated));
    }

    @Test
    void shouldFindTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        assertNotNull(service.getTrainerById(1L));
    }

    @Test
    void shouldReturnAllTrainers() {
        when(trainerRepository.findAll()).thenReturn(List.of(new Trainer()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void shouldDeleteTrainer() {
        when(trainerRepository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(trainerRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentTrainer() {
        when(trainerRepository.existsById(1L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void shouldThrowWhenCreatingTrainerWithBlankFirstName() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("  ");
        trainer.setLastName("Brown");
        assertThrows(IllegalArgumentException.class, () -> service.create(trainer));
    }

    @Test
    void shouldThrowWhenCreatingTrainerWithNullLastName() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Anna");
        trainer.setLastName(null);
        assertThrows(IllegalArgumentException.class, () -> service.create(trainer));
    }

    @Test
    void shouldThrowWhenTrainerNotFoundById() {
        when(trainerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getTrainerById(99L));
    }
}