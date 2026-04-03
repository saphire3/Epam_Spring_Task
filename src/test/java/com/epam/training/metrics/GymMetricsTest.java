package com.epam.training.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GymMetricsTest {

    private MeterRegistry registry;
    private GymMetrics gymMetrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        gymMetrics = new GymMetrics(registry);
    }

    @Test
    void incrementTraineeRegistrations_increasesCounter() {
        gymMetrics.incrementTraineeRegistrations();
        gymMetrics.incrementTraineeRegistrations();

        Counter counter = registry.find("gym.trainee.registrations").counter();
        assertNotNull(counter);
        assertEquals(2.0, counter.count());
    }

    @Test
    void incrementTrainerRegistrations_increasesCounter() {
        gymMetrics.incrementTrainerRegistrations();

        Counter counter = registry.find("gym.trainer.registrations").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void incrementTrainingsCreated_increasesCounter() {
        gymMetrics.incrementTrainingsCreated();
        gymMetrics.incrementTrainingsCreated();
        gymMetrics.incrementTrainingsCreated();

        Counter counter = registry.find("gym.training.created").counter();
        assertNotNull(counter);
        assertEquals(3.0, counter.count());
    }

    @Test
    void countersStartAtZero() {
        Counter trainee = registry.find("gym.trainee.registrations").counter();
        Counter trainer = registry.find("gym.trainer.registrations").counter();
        Counter training = registry.find("gym.training.created").counter();

        assertNotNull(trainee);
        assertNotNull(trainer);
        assertNotNull(training);
        assertEquals(0.0, trainee.count());
        assertEquals(0.0, trainer.count());
        assertEquals(0.0, training.count());
    }
}