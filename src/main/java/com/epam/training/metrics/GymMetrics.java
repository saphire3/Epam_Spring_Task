package com.epam.training.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GymMetrics {

    private final Counter traineeRegistrations;
    private final Counter trainerRegistrations;
    private final Counter trainingsCreated;

    public GymMetrics(MeterRegistry registry) {
        this.traineeRegistrations = Counter.builder("gym.trainee.registrations")
                .description("Total number of trainee registrations")
                .register(registry);

        this.trainerRegistrations = Counter.builder("gym.trainer.registrations")
                .description("Total number of trainer registrations")
                .register(registry);

        this.trainingsCreated = Counter.builder("gym.training.created")
                .description("Total number of trainings created")
                .register(registry);
    }

    public void incrementTraineeRegistrations() {
        traineeRegistrations.increment();
    }

    public void incrementTrainerRegistrations() {
        trainerRegistrations.increment();
    }

    public void incrementTrainingsCreated() {
        trainingsCreated.increment();
    }
}