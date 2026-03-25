package com.epam.training.config;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void init_savesDefaultTypes_whenEmpty() {
        when(trainingTypeDao.findAll()).thenReturn(Collections.emptyList());

        dataInitializer.init();

        verify(trainingTypeDao, times(5)).save(any(TrainingType.class));
    }

    @Test
    void init_doesNothing_whenAlreadyHasData() {
        TrainingType existing = new TrainingType();
        existing.setTrainingTypeName("FITNESS");
        when(trainingTypeDao.findAll()).thenReturn(List.of(existing));

        dataInitializer.init();

        verify(trainingTypeDao, never()).save(any());
    }

    @Test
    void init_runsOnlyOnce() {
        when(trainingTypeDao.findAll()).thenReturn(Collections.emptyList());

        dataInitializer.init();
        dataInitializer.init();

        verify(trainingTypeDao, times(5)).save(any(TrainingType.class));
    }
}