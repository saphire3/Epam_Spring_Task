package com.epam.training.health;

import com.epam.training.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("trainingServiceHealth")
public class TrainingServiceHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceHealthIndicator.class);

    private final TrainingService trainingService;

    public TrainingServiceHealthIndicator(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Override
    public Health health() {
        try {
            List<?> trainings = trainingService.findAll();

            log.debug("Health check: training service accessible, trainings.count={}", trainings.size());
            return Health.up()
                    .withDetail("trainings.accessible", true)
                    .withDetail("trainings.count", trainings.size())
                    .build();

        } catch (Exception e) {
            log.error("Training service health check failed: {}", e.getMessage());
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return Health.down()
                    .withDetail("trainings.accessible", false)
                    .withDetail("error", errorMsg)
                    .build();
        }
    }
}