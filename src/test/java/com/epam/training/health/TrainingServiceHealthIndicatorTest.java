package com.epam.training.health;

import com.epam.training.model.Training;
import com.epam.training.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceHealthIndicatorTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingServiceHealthIndicator healthIndicator;

    @Test
    void health_returnsUp_whenServiceRespondsWithTrainings() {
        Training training = new Training();
        training.setTrainingName("Morning Run");

        when(trainingService.findAll()).thenReturn(List.of(training));

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(true, health.getDetails().get("trainings.accessible"));
        assertEquals(1, health.getDetails().get("trainings.count"));
    }

    @Test
    void health_returnsUp_whenServiceRespondsWithEmptyList() {
        when(trainingService.findAll()).thenReturn(Collections.emptyList());

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(true, health.getDetails().get("trainings.accessible"));
        assertEquals(0, health.getDetails().get("trainings.count"));
    }

    @Test
    void health_returnsDown_whenServiceThrows() {
        when(trainingService.findAll()).thenThrow(new RuntimeException("Database error"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(false, health.getDetails().get("trainings.accessible"));
        assertEquals("Database error", health.getDetails().get("error"));
    }

    @Test
    void health_returnsDown_whenServiceThrowsWithNullMessage() {
        when(trainingService.findAll()).thenThrow(new RuntimeException());

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(false, health.getDetails().get("trainings.accessible"));
        assertEquals("RuntimeException", health.getDetails().get("error"));
    }
}