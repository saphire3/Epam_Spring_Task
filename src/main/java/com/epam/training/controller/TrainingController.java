package com.epam.training.controller;

import com.epam.training.dto.request.AddTrainingRequest;
import com.epam.training.model.Training;
import com.epam.training.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<String> addTraining(@Valid @RequestBody AddTrainingRequest request) {
        Training training = new Training();
        training.setTrainingName(request.getTrainingName());
        training.setTrainingDate(request.getTrainingDate());
        training.setDuration(request.getDuration());

        trainingService.create(
                request.getTraineeUsername(),
                request.getTraineePassword(),
                request.getTrainerUsername(),
                request.getTrainingTypeName(),
                training
        );

        return ResponseEntity.ok("Training added successfully");
    }
}