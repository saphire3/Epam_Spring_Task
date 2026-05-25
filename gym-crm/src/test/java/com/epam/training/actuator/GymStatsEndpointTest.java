package com.epam.training.actuator;

import com.epam.training.repository.TraineeRepository;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymStatsEndpointTest {

    @Mock TrainerRepository trainerRepository;
    @Mock TraineeRepository traineeRepository;
    @Mock TrainingRepository trainingRepository;
    @InjectMocks GymStatsEndpoint endpoint;

    @Test
    void shouldReturnStats() {
        when(trainerRepository.count()).thenReturn(3L);
        when(traineeRepository.count()).thenReturn(5L);
        when(trainingRepository.count()).thenReturn(10L);

        Map<String, Object> stats = endpoint.stats();

        assertEquals(3L, stats.get("trainers"));
        assertEquals(5L, stats.get("trainees"));
        assertEquals(10L, stats.get("trainings"));
    }
}