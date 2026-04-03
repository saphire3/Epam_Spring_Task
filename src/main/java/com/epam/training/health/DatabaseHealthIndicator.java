package com.epam.training.health;

import com.epam.training.service.TrainingTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component("database")
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(DatabaseHealthIndicator.class);

    private final DataSource dataSource;
    private final TrainingTypeService trainingTypeService;

    public DatabaseHealthIndicator(DataSource dataSource, TrainingTypeService trainingTypeService) {
        this.dataSource = dataSource;
        this.trainingTypeService = trainingTypeService;
    }

    @Override
    public Health health() {
        try {
            checkDatabaseConnection();
            int typeCount = trainingTypeService.findAll().size();

            if (typeCount == 0) {
                log.warn("Health check: database reachable but no training types seeded");
                return Health.down()
                        .withDetail("db.connection", "OK")
                        .withDetail("training_types.count", 0)
                        .withDetail("error", "No training types found — DataInitializer may not have run")
                        .build();
            }

            log.debug("Health check: database OK, training_types.count={}", typeCount);
            return Health.up()
                    .withDetail("db.connection", "OK")
                    .withDetail("training_types.count", typeCount)
                    .build();

        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            return Health.down()
                    .withDetail("error", errorMsg)
                    .build();
        }
    }

    private void checkDatabaseConnection() throws Exception {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1");
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                throw new IllegalStateException("Database ping returned no result");
            }
        }
    }
}