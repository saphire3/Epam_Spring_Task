package com.epam.training.service;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.repository.TraineeRepository;
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
class TraineeServiceTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private UsernameGenerator usernameGenerator;
    @Mock private PasswordGenerator passwordGenerator;
    @InjectMocks private TraineeService service;

    @Test
    void shouldCreateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");

        when(usernameGenerator.generate(any(), any())).thenReturn("john.smith");
        when(passwordGenerator.generate()).thenReturn("randomPass");
        when(traineeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainee result = service.create(trainee);

        assertEquals("john.smith", result.getUsername());
        verify(traineeRepository).save(any());
    }

    @Test
    void shouldUpdateTrainee() {
        Trainee existing = new Trainee();
        existing.setId(1L);
        existing.setFirstName("Old");
        existing.setLastName("Name");

        Trainee updated = new Trainee();
        updated.setFirstName("New");
        updated.setLastName("Name");

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(traineeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainee result = service.update(1L, updated);

        assertEquals("New", result.getFirstName());
        verify(traineeRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTraineeNotFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());
        Trainee updated = new Trainee();
        updated.setFirstName("New");
        updated.setLastName("Name");
        assertThrows(UserNotFoundException.class, () -> service.update(1L, updated));
    }

    @Test
    void shouldFindTrainee() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        assertNotNull(service.getTraineeById(1L));
    }

    @Test
    void shouldReturnAllTrainees() {
        when(traineeRepository.findAll()).thenReturn(List.of(new Trainee()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void shouldDeleteTrainee() {
        when(traineeRepository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(traineeRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentTrainee() {
        when(traineeRepository.existsById(1L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void shouldThrowWhenCreatingTraineeWithBlankFirstName() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("  ");
        trainee.setLastName("Smith");
        assertThrows(IllegalArgumentException.class, () -> service.create(trainee));
    }

    @Test
    void shouldThrowWhenCreatingTraineeWithNullLastName() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName(null);
        assertThrows(IllegalArgumentException.class, () -> service.create(trainee));
    }

    @Test
    void shouldThrowWhenTraineeNotFoundById() {
        when(traineeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getTraineeById(99L));
    }
}