package com.epam.training.mapper;

import com.epam.training.dto.response.TraineeProfileResponse;
import com.epam.training.dto.response.TraineeSummaryResponse;
import com.epam.training.dto.response.TrainerSummaryResponse;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;

import java.util.List;
import java.util.stream.Collectors;

public class TraineeMapper {

    private TraineeMapper() {
    }

    public static TraineeProfileResponse toProfileResponse(Trainee trainee) {
        TraineeProfileResponse response = new TraineeProfileResponse();
        response.setUsername(trainee.getUser().getUsername());
        response.setFirstName(trainee.getUser().getFirstName());
        response.setLastName(trainee.getUser().getLastName());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setActive(trainee.getUser().isActive());

        List<TrainerSummaryResponse> trainers = trainee.getTrainers()
                .stream()
                .map(TraineeMapper::toTrainerSummaryResponse)
                .collect(Collectors.toList());

        response.setTrainers(trainers);
        return response;
    }

    public static TrainerSummaryResponse toTrainerSummaryResponse(Trainer trainer) {
        TrainerSummaryResponse response = new TrainerSummaryResponse();
        response.setUsername(trainer.getUser().getUsername());
        response.setFirstName(trainer.getUser().getFirstName());
        response.setLastName(trainer.getUser().getLastName());
        response.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
        return response;
    }

    public static TraineeSummaryResponse toTraineeSummaryResponse(Trainee trainee) {
        TraineeSummaryResponse response = new TraineeSummaryResponse();
        response.setUsername(trainee.getUser().getUsername());
        response.setFirstName(trainee.getUser().getFirstName());
        response.setLastName(trainee.getUser().getLastName());
        return response;
    }
}