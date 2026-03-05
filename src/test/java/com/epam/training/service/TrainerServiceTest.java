package com.epam.training.service;

import com.epam.training.dao.TrainerDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainer;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerService service;

    @Test
    void shouldCreateTrainer() {

        Trainer trainer = new Trainer();
        trainer.setFirstName("Anna");
        trainer.setLastName("Brown");

        when(usernameGenerator.generate(any(), any()))
                .thenReturn("anna.brown");
        when(passwordGenerator.generate())
                .thenReturn("password123");

        Trainer result = service.create(trainer);

        assertEquals("anna.brown", result.getUsername());
        verify(trainerDao).save(anyLong(), any());
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

        when(trainerDao.getById(1L)).thenReturn(existing);

        Trainer result = service.update(1L, updated);

        assertEquals("New", result.getFirstName());
        verify(trainerDao).save(eq(1L), any());
    }

    @Test
    void shouldThrowExceptionWhenTrainerNotFound() {

        when(trainerDao.getById(1L)).thenReturn(null);

        Trainer updated = new Trainer();
        updated.setFirstName("New");
        updated.setLastName("Name");

        assertThrows(UserNotFoundException.class,
                () -> service.update(1L, updated));
    }

    @Test
    void shouldFindTrainer() {

        Trainer trainer = new Trainer();
        trainer.setId(1L);

        when(trainerDao.getById(1L)).thenReturn(trainer);

        assertNotNull(service.getTrainerById(1L));
    }

    @Test
    void shouldReturnAllTrainers() {

        when(trainerDao.findAll()).thenReturn(List.of(new Trainer()));

        assertEquals(1, service.findAll().size());
    }
}