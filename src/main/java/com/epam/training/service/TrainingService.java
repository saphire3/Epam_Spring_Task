package com.epam.training.service;

import com.epam.training.dao.TrainingDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {

    private final TrainingDao trainingDao;

    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public Training create(Training training) {

        Long id = (long) (trainingDao.findAll().size() + 1);
        training.setId(id);

        trainingDao.save(id, training);

        return training;
    }
    public void delete(Long id) {

        Training trainee = trainingDao.getById(id);

        if (trainee == null) {
            throw new UserNotFoundException(id);
        }

        trainingDao.delete(id);
    }
    public Training getTrainingById(Long id) {

        Training training = trainingDao.getById(id);

        if (training == null) {
            throw new UserNotFoundException(id);
        }

        return training;
    }

    public List<Training> findAll() {
        return trainingDao.findAll();
    }
}