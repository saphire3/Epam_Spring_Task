package com.epam.training.controller;

import com.epam.training.dto.request.TrainingRequest;
import com.epam.training.model.Training;
import com.epam.training.service.TrainingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private static final Logger log = LoggerFactory.getLogger(TrainingController.class);

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingRequest request) {
        trainingService.create(
                request.getTraineeUsername(),
                request.getTrainerUsername(),
                request.getTrainingTypeName(),
                request.getTrainingName(),
                request.getTrainingDate(),
                request.getDuration()
        );
        log.info("Created training '{}' for trainee '{}'",
                request.getTrainingName(), request.getTraineeUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
