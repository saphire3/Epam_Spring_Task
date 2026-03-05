package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeService service;

    @Test
    void shouldCreateTrainee() {

        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Smith");

        when(usernameGenerator.generate(any(), any()))
                .thenReturn("john.smith");
        when(passwordGenerator.generate())
                .thenReturn("randomPass");

        Trainee result = service.create(trainee);

        assertEquals("john.smith", result.getUsername());
        verify(traineeDao).save(anyLong(), any());
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

        when(traineeDao.getById(1L)).thenReturn(existing);

        Trainee result = service.update(1L, updated);

        assertEquals("New", result.getFirstName());
        verify(traineeDao).save(eq(1L), any());
    }

    @Test
    void shouldThrowExceptionWhenTraineeNotFound() {

        when(traineeDao.getById(1L)).thenReturn(null);

        Trainee updated = new Trainee();
        updated.setFirstName("New");
        updated.setLastName("Name");

        assertThrows(UserNotFoundException.class,
                () -> service.update(1L, updated));
    }

    @Test
    void shouldFindTrainee() {

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(traineeDao.getById(1L)).thenReturn(trainee);

        assertNotNull(service.getTraineeById(1L));
    }

    @Test
    void shouldDeleteTrainee() {

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(traineeDao.getById(1L)).thenReturn(trainee);

        service.delete(1L);

        verify(traineeDao).delete(1L);
    }
}