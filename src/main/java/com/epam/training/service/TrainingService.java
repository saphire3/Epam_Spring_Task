package com.epam.training.service;

import com.epam.training.dao.TrainingDao;
import com.epam.training.model.Training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);
    private static final AtomicLong idGenerator = new AtomicLong(1);

    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public Training create(Training training) {
        training.setId(idGenerator.getAndIncrement());
        trainingDao.save(training.getId(), training);

        log.info("Training created with id {}", training.getId());

        return training;
    }

    public Training find(Long id) {
        log.debug("Finding training with id {}", id);
        return trainingDao.findById(id);
    }
    public java.util.Collection<Training> findAll() {
        return trainingDao.findAll();
    }

}
