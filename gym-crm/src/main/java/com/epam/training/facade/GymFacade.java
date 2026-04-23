package com.epam.training.facade;

import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;

import org.springframework.stereotype.Component;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    // Constructor injection (REQUIRED by task)
    public GymFacade(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService) {

        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public TraineeService trainee() {
        return traineeService;
    }

    public TrainerService trainer() {
        return trainerService;
    }

    public TrainingService training() {
        return trainingService;
    }
}
