package com.epam.training.facade;

import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertSame;

class GymFacadeTest {

    @Test
    void getters_returnInjectedServices() {
        TraineeService traineeService = Mockito.mock(TraineeService.class);
        TrainerService trainerService = Mockito.mock(TrainerService.class);
        TrainingService trainingService = Mockito.mock(TrainingService.class);

        GymFacade facade = new GymFacade(traineeService, trainerService, trainingService);

        assertSame(traineeService, facade.trainee());
        assertSame(trainerService, facade.trainer());
        assertSame(trainingService, facade.training());
    }
}