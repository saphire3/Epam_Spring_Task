package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.dto.TraineeTrainingFilter;
import com.epam.training.dto.TrainerTrainingFilter;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.model.TrainingType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingDao trainingDao;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final AuthService authService;

    public TrainingService(TrainingDao trainingDao,
                           TraineeDao traineeDao,
                           TrainerDao trainerDao,
                           TrainingTypeDao trainingTypeDao,
                           AuthService authService) {
        this.trainingDao = trainingDao;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.trainingTypeDao = trainingTypeDao;
        this.authService = authService;
    }

    public Training create(String traineeUsername,
                           String traineePassword,
                           String trainerUsername,
                           String trainingTypeName,
                           Training training) {

        validateUsername(traineeUsername);
        validateUsername(trainerUsername);
        authService.requireTraineeAuth(traineeUsername, traineePassword);
        validateTraining(training);

        if (StringUtils.isBlank(trainingTypeName)) {
            throw new IllegalArgumentException("Training type cannot be blank");
        }

        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + traineeUsername));

        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + trainerUsername));

        TrainingType type = trainingTypeDao.findByName(trainingTypeName)
                .orElseThrow(() -> new IllegalArgumentException("Training type not found: " + trainingTypeName));

        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(type);

        trainingDao.save(training);
        log.info("Created training '{}' for trainee={} with trainer={}",
                training.getTrainingName(), traineeUsername, trainerUsername);

        return training;
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String username,
                                              String password,
                                              TraineeTrainingFilter filter) {
        validateUsername(username);
        authService.requireTraineeAuth(username, password);

        TraineeTrainingFilter safeFilter = filter == null ? new TraineeTrainingFilter() : filter;
        List<Training> result = trainingDao.findTraineeTrainings(username, safeFilter);

        log.info("Fetched {} trainings for trainee={}", result.size(), username);
        return result;
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(String username,
                                              String password,
                                              TrainerTrainingFilter filter) {
        validateUsername(username);
        authService.requireTrainerAuth(username, password);

        TrainerTrainingFilter safeFilter = filter == null ? new TrainerTrainingFilter() : filter;
        List<Training> result = trainingDao.findTrainerTrainings(username, safeFilter);

        log.info("Fetched {} trainings for trainer={}", result.size(), username);
        return result;
    }

    @Transactional(readOnly = true)
    public List<Training> findAll() {
        return trainingDao.findAll();
    }

    private void validateTraining(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training cannot be null");
        }
        if (StringUtils.isBlank(training.getTrainingName())) {
            throw new IllegalArgumentException("Training name cannot be blank");
        }
        if (training.getTrainingDate() == null) {
            throw new IllegalArgumentException("Training date cannot be null");
        }
        if (training.getDuration() == null || training.getDuration() <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }

    private void validateUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
    }
}