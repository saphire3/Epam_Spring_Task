package com.epam.training.controller;

import com.epam.training.dto.filter.TrainerTrainingFilter;
import com.epam.training.dto.request.TrainerRegistrationRequest;
import com.epam.training.dto.request.TrainerTrainingsRequest;
import com.epam.training.dto.request.UpdateActiveStatusRequest;
import com.epam.training.dto.request.UpdateTrainerRequest;
import com.epam.training.dto.response.RegistrationResponse;
import com.epam.training.dto.response.TrainerProfileResponse;
import com.epam.training.dto.response.TrainerTrainingResponse;
import com.epam.training.exception.BadRequestException;
import com.epam.training.mapper.TrainerMapper;
import com.epam.training.mapper.TrainingMapper;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public TrainerController(TrainerService trainerService,
                             TrainingService trainingService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TrainerRegistrationRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Trainer created = trainerService.create(trainer, request.getSpecializationName());

        return ResponseEntity.ok(new RegistrationResponse(
                created.getUser().getUsername(),
                created.getUser().getPassword()
        ));
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getProfile(@PathVariable String username,
                                                             @RequestParam String password) {
        Trainer trainer = trainerService.findByUsername(username, password);
        return ResponseEntity.ok(TrainerMapper.toProfileResponse(trainer));
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> update(@PathVariable String username,
                                                         @Valid @RequestBody UpdateTrainerRequest request) {
        if (!username.equals(request.getUsername())) {
            throw new BadRequestException("Username cannot be changed");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Trainer updated = trainerService.update(
                request.getUsername(),
                request.getPassword(),
                trainer,
                null
        );

        return ResponseEntity.ok(TrainerMapper.toProfileResponse(updated));
    }

    @PatchMapping("/{username}/active")
    public ResponseEntity<String> updateActiveStatus(@PathVariable String username,
                                                     @Valid @RequestBody UpdateActiveStatusRequest request) {
        if (!username.equals(request.getUsername())) {
            throw new BadRequestException("Username cannot be changed");
        }

        if (Boolean.TRUE.equals(request.getActive())) {
            trainerService.activate(request.getUsername(), request.getPassword());
            return ResponseEntity.ok("Trainer activated successfully");
        } else {
            trainerService.deactivate(request.getUsername(), request.getPassword());
            return ResponseEntity.ok("Trainer deactivated successfully");
        }
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainings(@PathVariable String username,
                                                                      @Valid @ModelAttribute TrainerTrainingsRequest request) {
        if (!username.equals(request.getUsername())) {
            throw new BadRequestException("Username cannot be changed");
        }

        TrainerTrainingFilter filter = new TrainerTrainingFilter();
        filter.setPeriodFrom(request.getPeriodFrom());
        filter.setPeriodTo(request.getPeriodTo());
        filter.setTraineeName(request.getTraineeName());

        List<TrainerTrainingResponse> response = trainingService
                .getTrainerTrainings(request.getUsername(), request.getPassword(), filter)
                .stream()
                .map(TrainingMapper::toTrainerTrainingResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}