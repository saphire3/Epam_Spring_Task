package com.epam.training.controller;

import com.epam.training.dto.request.*;
import com.epam.training.dto.response.*;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;
import com.epam.training.service.TrainerService;
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
@RequestMapping("/api/trainers")
public class TrainerController {

    private static final Logger log = LoggerFactory.getLogger(TrainerController.class);

    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public TrainerController(TrainerService trainerService, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TrainerRegistrationRequest request) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());

        String[] result = trainerService.create(trainer, request.getSpecializationName());
        log.info("Registered trainer: {}", result[0]);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrationResponse(result[0], result[1]));
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getProfile(@PathVariable String username) {
        Trainer trainer = trainerService.findByUsername(username);
        return ResponseEntity.ok(toProfileResponse(trainer));
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> updateProfile(
            @PathVariable String username,
            @Valid @RequestBody UpdateTrainerRequest request) {
        Trainer updated = new Trainer();
        updated.setFirstName(request.getFirstName());
        updated.setLastName(request.getLastName());

        Trainer saved = trainerService.update(username, updated, request.getSpecializationName());
        return ResponseEntity.ok(toProfileResponse(saved));
    }

    @PatchMapping("/{username}/active")
    public ResponseEntity<Void> setActiveStatus(
            @PathVariable String username,
            @Valid @RequestBody ActiveStatusRequest request) {
        if (Boolean.TRUE.equals(request.getIsActive())) {
            trainerService.activate(username);
        } else {
            trainerService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String traineeName) {

        List<Training> trainings = trainingService.getTrainerTrainings(
                username, fromDate, toDate, traineeName);

        List<TrainerTrainingResponse> response = trainings.stream()
                .map(t -> new TrainerTrainingResponse(
                        t.getTrainingName(),
                        t.getTrainingDate(),
                        t.getTrainingType() != null ? t.getTrainingType().getTrainingTypeName() : null,
                        t.getDuration(),
                        t.getTrainee() != null ? t.getTrainee().getUsername() : null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private TrainerProfileResponse toProfileResponse(Trainer trainer) {
        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setFirstName(trainer.getFirstName());
        response.setLastName(trainer.getLastName());
        response.setSpecializationName(
                trainer.getSpecialization() != null ? trainer.getSpecialization().getTrainingTypeName() : null);
        response.setActive(trainer.isActive());
        response.setTrainees(
                trainer.getTrainees().stream()
                        .map(te -> new TraineeSummaryResponse(
                                te.getUsername(),
                                te.getFirstName(),
                                te.getLastName()))
                        .collect(Collectors.toList())
        );
        return response;
    }
}
