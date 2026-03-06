package com.epam.training.facade;

import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class GymFacadeTest {

    @Test
    void shouldReturnServices() {
        TraineeService traineeService = mock(TraineeService.class);
        TrainerService trainerService = mock(TrainerService.class);
        TrainingService trainingService = mock(TrainingService.class);

        GymFacade facade = new GymFacade(traineeService, trainerService, trainingService);

        assertNotNull(facade.trainee());
        assertNotNull(facade.trainer());
        assertNotNull(facade.training());
    }
}