package com.epam.training.util;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UsernameGenerator {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public UsernameGenerator(TraineeDao traineeDao, TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    @Transactional(readOnly = true)
    public String generate(String firstName, String lastName) {
        String base = (firstName + "." + lastName).trim().toLowerCase().replace(" ", "");
        String candidate = base;
        int suffix = 1;

        while (exists(candidate)) {
            candidate = base + suffix++;
        }

        return candidate;
    }

    private boolean exists(String username) {
        return traineeDao.findByUsername(username).isPresent()
                || trainerDao.findByUsername(username).isPresent();
    }
}