package com.epam.training.actuator;

import com.epam.training.repository.TraineeRepository;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "gym-stats")
public class GymStatsEndpoint {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;

    public GymStatsEndpoint(TrainerRepository trainerRepository,
                             TraineeRepository traineeRepository,
                             TrainingRepository trainingRepository) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingRepository = trainingRepository;
    }

    @ReadOperation
    public Map<String, Object> stats() {
        return Map.of(
                "trainers", trainerRepository.count(),
                "trainees", traineeRepository.count(),
                "trainings", trainingRepository.count()
        );
    }
}