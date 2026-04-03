package com.epam.training.controller;

import com.epam.training.dto.request.*;
import com.epam.training.dto.response.*;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private static final Logger log = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public TraineeController(TraineeService traineeService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TraineeRegistrationRequest request) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());

        String[] result = traineeService.create(trainee);
        log.info("Registered trainee: {}", result[0]);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrationResponse(result[0], result[1]));
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getProfile(@PathVariable String username) {
        Trainee trainee = traineeService.findByUsername(username);
        TraineeProfileResponse response = toProfileResponse(trainee);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> updateProfile(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeRequest request) {
        Trainee updated = new Trainee();
        updated.setFirstName(request.getFirstName());
        updated.setLastName(request.getLastName());
        updated.setDateOfBirth(request.getDateOfBirth());
        updated.setAddress(request.getAddress());

        Trainee saved = traineeService.update(username, updated);
        return ResponseEntity.ok(toProfileResponse(saved));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteProfile(@PathVariable String username) {
        traineeService.delete(username);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{username}/active")
    public ResponseEntity<Void> setActiveStatus(
            @PathVariable String username,
            @Valid @RequestBody ActiveStatusRequest request) {
        if (Boolean.TRUE.equals(request.getIsActive())) {
            traineeService.activate(username);
        } else {
            traineeService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> getUnassignedTrainers(@PathVariable String username) {
        List<Trainer> trainers = traineeService.getUnassignedTrainers(username);
        List<TrainerSummaryResponse> response = trainers.stream()
                .map(t -> new TrainerSummaryResponse(
                        t.getUsername(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getSpecialization() != null ? t.getSpecialization().getTrainingTypeName() : null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> updateTrainers(
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainersRequest request) {
        List<Trainer> trainers = traineeService.updateTrainerList(username, request.getTrainerUsernames());
        List<TrainerSummaryResponse> response = trainers.stream()
                .map(t -> new TrainerSummaryResponse(
                        t.getUsername(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getSpecialization() != null ? t.getSpecialization().getTrainingTypeName() : null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType) {

        List<Training> trainings = trainingService.getTraineeTrainings(
                username, fromDate, toDate, trainerName, trainingType);

        List<TraineeTrainingResponse> response = trainings.stream()
                .map(t -> new TraineeTrainingResponse(
                        t.getTrainingName(),
                        t.getTrainingDate(),
                        t.getTrainingType() != null ? t.getTrainingType().getTrainingTypeName() : null,
                        t.getDuration(),
                        t.getTrainer() != null ? t.getTrainer().getUsername() : null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private TraineeProfileResponse toProfileResponse(Trainee trainee) {
        TraineeProfileResponse response = new TraineeProfileResponse();
        response.setFirstName(trainee.getFirstName());
        response.setLastName(trainee.getLastName());
        response.setDateOfBirth(trainee.getDateOfBirth());
        response.setAddress(trainee.getAddress());
        response.setActive(trainee.isActive());
        response.setTrainers(
                trainee.getTrainers().stream()
                        .map(t -> new TrainerSummaryResponse(
                                t.getUsername(),
                                t.getFirstName(),
                                t.getLastName(),
                                t.getSpecialization() != null ? t.getSpecialization().getTrainingTypeName() : null))
                        .collect(Collectors.toList())
        );
        return response;
    }
}
