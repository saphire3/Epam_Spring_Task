package com.epam.training.service;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.messaging.WorkloadMessageProducer;
import com.epam.training.model.Training;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingRepository;
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
class TrainingServiceTest {

    @Mock private TrainingRepository trainingRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private WorkloadMessageProducer workloadMessageProducer;
    @InjectMocks private TrainingService service;

    @Test
    void shouldCreateTraining() {
        Training training = new Training();
        when(trainingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Training result = service.create(training);
        assertNotNull(result);
        verify(trainingRepository).save(any());
    }

    @Test
    void shouldFindTraining() {
        Training training = new Training();
        training.setId(1L);
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        assertNotNull(service.getTrainingById(1L));
    }

    @Test
    void shouldThrowExceptionWhenTrainingNotFound() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getTrainingById(1L));
    }

    @Test
    void shouldReturnAllTrainings() {
        when(trainingRepository.findAll()).thenReturn(List.of(new Training()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void shouldDeleteTraining() {
        Training training = new Training();
        training.setId(1L);
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        service.delete(1L);
        verify(trainingRepository).deleteById(1L);
    }
}