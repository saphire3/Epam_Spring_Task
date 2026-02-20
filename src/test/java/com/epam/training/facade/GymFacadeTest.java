package com.epam.training.facade;

import com.epam.training.service.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GymFacadeTest {

    @Test
    void shouldReturnServices() {

        TraineeService traineeService = new TraineeService();
        TrainerService trainerService = new TrainerService();
        TrainingService trainingService = new TrainingService();

        GymFacade facade = new GymFacade(
                traineeService,
                trainerService,
                trainingService
        );

        assertNotNull(facade.trainee());
        assertNotNull(facade.trainer());
        assertNotNull(facade.training());
    }
}