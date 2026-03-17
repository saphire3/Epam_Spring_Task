package com.epam.training.service;

import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.model.TrainingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypeService(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    public void initializeDefaultTrainingTypes() {
        saveIfMissing("Fitness");
        saveIfMissing("Yoga");
        saveIfMissing("Cardio");
    }

    private void saveIfMissing(String name) {
        if (trainingTypeDao.findByName(name).isEmpty()) {
            TrainingType trainingType = new TrainingType();
            trainingType.setTrainingTypeName(name);
            trainingTypeDao.save(trainingType);
            System.out.println("Initialized training type: " + name);
        }
    }
}