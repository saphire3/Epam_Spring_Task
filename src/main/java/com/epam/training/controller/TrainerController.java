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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@Api(tags = "Trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public TrainerController(TrainerService trainerService,
                             TrainingService trainingService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @PostMapping("/register")
    @ApiOperation("Register a trainer")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer registered successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 409, message = "Conflict")
    })
    public ResponseEntity<RegistrationResponse> register(
            @ApiParam("Trainer registration request")
            @Valid @RequestBody TrainerRegistrationRequest request) {

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
    @ApiOperation("Get trainer profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer profile returned"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @ApiParam("Trainer username") @PathVariable String username,
            @ApiParam("User password") @RequestParam String password) {

        Trainer trainer = trainerService.findByUsername(username, password);
        return ResponseEntity.ok(TrainerMapper.toProfileResponse(trainer));
    }

    @PutMapping("/{username}")
    @ApiOperation("Update trainer profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer updated successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<TrainerProfileResponse> update(
            @ApiParam("Trainer username") @PathVariable String username,
            @ApiParam("Update trainer request") @Valid @RequestBody UpdateTrainerRequest request) {

        if (!username.equals(request.getUsername())) {
            throw new BadRequestException("Username cannot be changed");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(Boolean.TRUE.equals(request.getActive()));

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
    @ApiOperation("Activate or deactivate trainer")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer status updated"),
            @ApiResponse(code = 400, message = "Validation or state error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    public ResponseEntity<String> updateActiveStatus(
            @ApiParam("Trainer username") @PathVariable String username,
            @ApiParam("Update active status request") @Valid @RequestBody UpdateActiveStatusRequest request) {

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
    @ApiOperation("Get trainer trainings by filters")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer trainings returned"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainings(
            @ApiParam("Trainer username") @PathVariable String username,
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