package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public AuthService(TraineeDao traineeDao, TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    public boolean authenticateTrainee(String username, String password) {
        validateCredentials(username, password);

        return traineeDao.findByUsername(username)
                .map(t -> t.getUser().getPassword().equals(password))
                .orElse(false);
    }

    public boolean authenticateTrainer(String username, String password) {
        validateCredentials(username, password);

        return trainerDao.findByUsername(username)
                .map(t -> t.getUser().getPassword().equals(password))
                .orElse(false);
    }

    public void requireTraineeAuth(String username, String password) {
        if (!authenticateTrainee(username, password)) {
            throw new IllegalArgumentException("Invalid trainee username or password");
        }
    }

    public void requireTrainerAuth(String username, String password) {
        if (!authenticateTrainer(username, password)) {
            throw new IllegalArgumentException("Invalid trainer username or password");
        }
    }

    private void validateCredentials(String username, String password) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
    }
}