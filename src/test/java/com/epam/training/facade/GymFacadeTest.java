package com.epam.training.facade;

import com.epam.training.dao.*;
import com.epam.training.service.*;
import com.epam.training.storage.InMemoryStorage;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GymFacadeTest {

    @Test
    void shouldReturnServices() {

        // Storage
        InMemoryStorage storage = new InMemoryStorage();

        // DAOs
        TraineeDao traineeDao = new TraineeDao(storage);
        TrainerDao trainerDao = new TrainerDao(storage);
        TrainingDao trainingDao = new TrainingDao(storage);

        // Utils
        UsernameGenerator usernameGenerator =
                new UsernameGenerator(traineeDao, trainerDao);
        PasswordGenerator passwordGenerator =
                new PasswordGenerator();

        // Services
        TraineeService traineeService =
                new TraineeService(traineeDao, usernameGenerator, passwordGenerator);

        TrainerService trainerService =
                new TrainerService(trainerDao, usernameGenerator, passwordGenerator);

        TrainingService trainingService =
                new TrainingService(trainingDao);

        // Facade
        GymFacade facade =
                new GymFacade(traineeService, trainerService, trainingService);

        assertNotNull(facade.trainee());
        assertNotNull(facade.trainer());
        assertNotNull(facade.training());
    }
}