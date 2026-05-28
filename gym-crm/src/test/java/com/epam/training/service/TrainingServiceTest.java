package com.epam.training.service;

import com.epam.training.dto.ActionType;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.messaging.WorkloadMessageProducer;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @Test
    void shouldCreateTraining_withTrainer_sendsWorkloadNotification() {
        Training training = new Training();
        training.setTrainerId(1L);
        training.setTrainingDate(LocalDate.of(2024, 3, 15));
        training.setDuration(60);

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("john.doe");
        trainer.setActive(true);

        when(trainingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        service.create(training);

        verify(workloadMessageProducer).sendWorkloadUpdate(any());
    }

    @Test
    void shouldCreateTraining_withoutTrainerId_skipsWorkloadNotification() {
        Training training = new Training();
        training.setTrainingDate(LocalDate.of(2024, 3, 15));
        training.setDuration(60);

        when(trainingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.create(training);

        verify(workloadMessageProducer, never()).sendWorkloadUpdate(any());
    }

    @Test
    void shouldDeleteTraining_withTrainer_sendsDeleteNotification() {
        Training training = new Training();
        training.setId(1L);
        training.setTrainerId(2L);
        training.setTrainingDate(LocalDate.of(2024, 3, 15));
        training.setDuration(90);

        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainer.setFirstName("Anna");
        trainer.setLastName("Brown");
        trainer.setUsername("anna.brown");
        trainer.setActive(true);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(trainer));

        service.delete(1L);

        verify(trainingRepository).deleteById(1L);
        verify(workloadMessageProducer).sendWorkloadUpdate(any());
    }

    @Test
    void shouldDeleteTraining_trainerNotFound_skipsWorkloadNotification() {
        Training training = new Training();
        training.setId(1L);
        training.setTrainerId(2L);
        training.setTrainingDate(LocalDate.of(2024, 3, 15));
        training.setDuration(90);

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainerRepository.findById(2L)).thenReturn(Optional.empty());

        service.delete(1L);

        verify(trainingRepository).deleteById(1L);
        verify(workloadMessageProducer, never()).sendWorkloadUpdate(any());
    }

    @Test
    void shouldThrowWhenDeletingNonExistentTraining() {
        when(trainingRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.delete(99L));
    }
}