package com.epam.training.facade;

import com.epam.training.repository.TraineeRepository;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingRepository;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GymFacadeTest {

    @Test
    void shouldReturnServices() {
        TraineeRepository traineeRepo = mock(TraineeRepository.class);
        TrainerRepository trainerRepo = mock(TrainerRepository.class);
        TrainingRepository trainingRepo = mock(TrainingRepository.class);

        UsernameGenerator usernameGenerator = new UsernameGenerator(traineeRepo, trainerRepo);
        PasswordGenerator passwordGenerator = new PasswordGenerator();

        TraineeService traineeService = new TraineeService(traineeRepo, usernameGenerator, passwordGenerator);
        TrainerService trainerService = new TrainerService(trainerRepo, usernameGenerator, passwordGenerator);
        TrainingService trainingService = new TrainingService(trainingRepo);

        GymFacade facade = new GymFacade(traineeService, trainerService, trainingService);

        assertNotNull(facade.trainee());
        assertNotNull(facade.trainer());
        assertNotNull(facade.training());
    }
}