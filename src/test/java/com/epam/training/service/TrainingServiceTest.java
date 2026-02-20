package com.epam.training.service;

import com.epam.training.dao.TrainingDao;
import com.epam.training.model.Training;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingService service;

    @Test
    void shouldCreateTraining() {

        Training training = new Training();

        Training result = service.create(training);

        assertNotNull(result.getId());
        verify(trainingDao).save(anyLong(), any());
    }
    @Test
    void shouldFindTraining() {

        Training training = new Training();
        training.setId(1L);

        when(trainingDao.findById(1L)).thenReturn(training);

        assertNotNull(service.find(1L));
    }
    @Test
    void shouldReturnAllTrainings() {

        when(trainingDao.findAll()).thenReturn(java.util.List.of(new Training()));

        assertEquals(1, service.findAll().size());
    }
}
