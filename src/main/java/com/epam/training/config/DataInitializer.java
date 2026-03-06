package com.epam.training.config;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.model.TrainingType;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer {

    private final TrainingTypeDao trainingTypeDao;
    private boolean initialized = false;

    public DataInitializer(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void init() {
        if (initialized) {
            return;
        }

        if (!trainingTypeDao.findAll().isEmpty()) {
            initialized = true;
            return;
        }

        List<String> names = List.of("FITNESS", "YOGA", "CARDIO", "STRENGTH", "PILATES");

        for (String name : names) {
            TrainingType type = new TrainingType();
            type.setTrainingTypeName(name);
            trainingTypeDao.save(type);
        }

        initialized = true;
    }
}