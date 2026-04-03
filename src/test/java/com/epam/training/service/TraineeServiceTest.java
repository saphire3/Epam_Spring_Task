package com.epam.training.service;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.repository.TraineeRepository;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeService traineeService;

    @Test
    void shouldCreateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");

        when(usernameGenerator.generate("John", "Smith")).thenReturn("john.smith");
        when(passwordGenerator.generate()).thenReturn("rawpass123");
        when(passwordEncoder.encode("rawpass123")).thenReturn("$2a$10$encoded");
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        String[] result = traineeService.create(trainee);

        assertEquals("john.smith", result[0]);
        assertEquals("rawpass123", result[1]);
        assertEquals("john.smith", trainee.getUsername());
        assertEquals("$2a$10$encoded", trainee.getPassword());
        assertTrue(trainee.isActive());
    }

    @Test
    void shouldFindByUsername() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.findByUsername("john.smith");
        assertEquals("john.smith", result.getUsername());
    }

    @Test
    void shouldThrowWhenTraineeNotFound() {
        when(traineeRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> traineeService.findByUsername("unknown"));
    }

    @Test
    void shouldUpdateTrainee() {
        Trainee existing = new Trainee();
        existing.setUsername("john.smith");
        existing.setFirstName("John");
        existing.setLastName("Smith");

        Trainee updated = new Trainee();
        updated.setFirstName("Johnny");
        updated.setLastName("Smith");
        updated.setAddress("New York");
        updated.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(existing));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(existing);

        Trainee result = traineeService.update("john.smith", updated);

        assertEquals("Johnny", result.getFirstName());
        assertEquals("New York", result.getAddress());
    }

    @Test
    void shouldDeleteTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        traineeService.delete("john.smith");

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void shouldActivateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");
        trainee.setActive(false);

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        traineeService.activate("john.smith");

        assertTrue(trainee.isActive());
    }

    @Test
    void shouldDeactivateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");
        trainee.setActive(true);

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        traineeService.deactivate("john.smith");

        assertFalse(trainee.isActive());
    }

    @Test
    void shouldChangePassword() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.encode("newpass")).thenReturn("$2a$10$newencoded");
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        traineeService.changePassword("john.smith", "newpass");

        assertEquals("$2a$10$newencoded", trainee.getPassword());
    }

    @Test
    void shouldGetUnassignedTrainers() {
        List<Trainer> trainers = List.of(new Trainer());
        when(traineeRepository.findTrainersNotAssignedToTrainee("john.smith")).thenReturn(trainers);

        List<Trainer> result = traineeService.getUnassignedTrainers("john.smith");
        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateTrainerList() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john.smith");

        Trainer trainer = new Trainer();
        trainer.setUsername("anna.brown");

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsernameIn(List.of("anna.brown"))).thenReturn(List.of(trainer));
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        List<Trainer> result = traineeService.updateTrainerList("john.smith", List.of("anna.brown"));

        assertEquals(1, result.size());
        assertEquals("anna.brown", result.get(0).getUsername());
    }
}
