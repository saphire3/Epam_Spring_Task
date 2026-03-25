package com.epam.training.controller;

import com.epam.training.dto.request.AddTrainingRequest;
import com.epam.training.model.Training;
import com.epam.training.service.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainings")
@Api(tags = "Trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    @ApiOperation("Add training")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Training added successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee or trainer not found")
    })
    public ResponseEntity<String> addTraining(
            @ApiParam("Add training request")
            @Valid @RequestBody AddTrainingRequest request) {
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