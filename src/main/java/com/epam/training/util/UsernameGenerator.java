package com.epam.training.util;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UsernameGenerator {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public UsernameGenerator(TraineeDao traineeDao,
                             TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    public String generate(String firstName, String lastName) {

        Set<String> existingUsernames =
                Stream.concat(
                        traineeDao.findAll().stream().map(Trainee::getUsername),
                        trainerDao.findAll().stream().map(Trainer::getUsername)
                ).collect(Collectors.toSet());

        String base = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = base;
        int counter = 1;

        while (existingUsernames.contains(username)) {
            username = base + counter++;
        }

        return username;
    }
}