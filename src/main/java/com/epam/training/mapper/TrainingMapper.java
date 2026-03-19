package com.epam.training.mapper;

import com.epam.training.dto.response.TraineeTrainingResponse;
import com.epam.training.dto.response.TrainerTrainingResponse;
import com.epam.training.model.Training;

public class TrainingMapper {

    private TrainingMapper() {
    }

    public static TraineeTrainingResponse toTraineeTrainingResponse(Training training) {
        TraineeTrainingResponse response = new TraineeTrainingResponse();
        response.setTrainingName(training.getTrainingName());
        response.setTrainingDate(training.getTrainingDate());
        response.setTrainingType(training.getTrainingType().getTrainingTypeName());
        response.setDuration(training.getDuration());
        response.setTrainerName(
                training.getTrainer().getUser().getFirstName() + " " +
                        training.getTrainer().getUser().getLastName()
        );
        return response;
    }

    public static TrainerTrainingResponse toTrainerTrainingResponse(Training training) {
        TrainerTrainingResponse response = new TrainerTrainingResponse();
        response.setTrainingName(training.getTrainingName());
        response.setTrainingDate(training.getTrainingDate());
        response.setTrainingType(training.getTrainingType().getTrainingTypeName());
        response.setDuration(training.getDuration());
        response.setTraineeName(
                training.getTrainee().getUser().getFirstName() + " " +
                        training.getTrainee().getUser().getLastName()
        );
        return response;
    }
}