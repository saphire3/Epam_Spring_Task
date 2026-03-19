package com.epam.training.mapper;

import com.epam.training.dto.response.TraineeSummaryResponse;
import com.epam.training.dto.response.TrainerProfileResponse;
import com.epam.training.model.Trainer;

import java.util.List;
import java.util.stream.Collectors;

public class TrainerMapper {

    private TrainerMapper() {
    }

    public static TrainerProfileResponse toProfileResponse(Trainer trainer) {
        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setUsername(trainer.getUser().getUsername());
        response.setFirstName(trainer.getUser().getFirstName());
        response.setLastName(trainer.getUser().getLastName());
        response.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
        response.setActive(trainer.getUser().isActive());

        List<TraineeSummaryResponse> trainees = trainer.getTrainees()
                .stream()
                .map(TraineeMapper::toTraineeSummaryResponse)
                .collect(Collectors.toList());

        response.setTrainees(trainees);
        return response;
    }
}