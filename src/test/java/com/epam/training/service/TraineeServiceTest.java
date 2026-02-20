package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.exception.EntityNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.storage.InMemoryStorage;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

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

        when(traineeDao.findAll()).thenReturn(List.of());
        when(usernameGenerator.generate(any(), any(), any())).thenReturn("John.Smith");
        when(passwordGenerator.generate()).thenReturn("randomPass");

        Trainee result = service.create(trainee);

        assertEquals("John.Smith", result.getUsername());
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

        when(traineeDao.findById(1L)).thenReturn(existing);

        Trainee result = service.update(1L, updated);

        assertEquals("New", result.getFirstName());
        verify(traineeDao).save(eq(1L), any());
    }
    @Test
    void shouldThrowExceptionWhenTraineeNotFound() {
        when(traineeDao.findById(1L)).thenReturn(null);

        Trainee updated = new Trainee();
        updated.setFirstName("New");
        updated.setLastName("Name");

        assertThrows(EntityNotFoundException.class,
                () -> service.update(1L, updated));
    }

    @Test
    void shouldGenerateDifferentUsernameIfDuplicateExists() {

        Trainee existing = new Trainee();
        existing.setUsername("John.Smith");

        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("John");
        newTrainee.setLastName("Smith");

        when(traineeDao.findAll()).thenReturn(List.of(existing));
        when(usernameGenerator.generate(any(), any(), any()))
                .thenReturn("John.Smith1");
        when(passwordGenerator.generate())
                .thenReturn("password123");

        Trainee result = service.create(newTrainee);

        assertEquals("John.Smith1", result.getUsername());
    }
    @Test
    void shouldReturnAllTrainees() {

        InMemoryStorage storage = new InMemoryStorage();
        TraineeDao dao = new TraineeDao();
        dao.setStorage(storage);

        dao.save(1L, new Trainee());

        assertEquals(1, dao.findAll().size());
    }
    @Test
    void shouldThrowExceptionWhenFirstNameIsNull() {

        Trainee trainee = new Trainee();
        trainee.setFirstName(null);
        trainee.setLastName("Smith");

        assertThrows(IllegalArgumentException.class,
                () -> service.create(trainee));
    }
    @Test
    void shouldFindTrainee() {

        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(traineeDao.findById(1L)).thenReturn(trainee);

        assertNotNull(service.find(1L));
    }

    @Test
    void shouldDeleteTrainee() {

        service.delete(1L);

        verify(traineeDao).delete(1L);
    }

}
