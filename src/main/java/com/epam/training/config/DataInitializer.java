package com.epam.training.config;

import com.epam.training.model.TrainingType;
import com.epam.training.repository.TrainingTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final TrainingTypeRepository trainingTypeRepository;

    public DataInitializer(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public void run(String... args) {
        List<String> trainingTypes = List.of("FITNESS", "YOGA", "CARDIO", "STRENGTH", "PILATES");

        for (String typeName : trainingTypes) {
            if (trainingTypeRepository.findByTrainingTypeName(typeName).isEmpty()) {
                trainingTypeRepository.save(new TrainingType(typeName));
                log.info("Seeded training type: {}", typeName);
            }
        }
    }
}
