package com.epam.training.controller;

import com.epam.training.dto.filter.TraineeTrainingFilter;
import com.epam.training.dto.request.TraineeRegistrationRequest;
import com.epam.training.dto.request.TraineeTrainingsRequest;
import com.epam.training.dto.request.UpdateActiveStatusRequest;
import com.epam.training.dto.request.UpdateTraineeRequest;
import com.epam.training.dto.request.UpdateTraineeTrainerListRequest;
import com.epam.training.dto.response.RegistrationResponse;
import com.epam.training.dto.response.TraineeProfileResponse;
import com.epam.training.dto.response.TraineeTrainingResponse;
import com.epam.training.dto.response.TrainerSummaryResponse;
import com.epam.training.exception.BadRequestException;
import com.epam.training.mapper.TraineeMapper;
import com.epam.training.mapper.TrainingMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import com.epam.training.service.TraineeService;
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
@RequestMapping("/api/trainees")
@Api(tags = "Trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public TraineeController(TraineeService traineeService,
                             TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    @PostMapping("/register")
    @ApiOperation("Register a trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee registered successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 409, message = "Conflict")
    })
    public ResponseEntity<RegistrationResponse> register(
            @ApiParam("Trainee registration request")
            @Valid @RequestBody TraineeRegistrationRequest request) {

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());

        Trainee created = traineeService.create(trainee);

        return ResponseEntity.ok(new RegistrationResponse(
                created.getUser().getUsername(),
                created.getUser().getPassword()
        ));
    }

    @GetMapping("/{username}")
    @ApiOperation("Get trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee profile returned"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<TraineeProfileResponse> getProfile(
            @ApiParam("Trainee username") @PathVariable String username,
            @ApiParam("User password") @RequestParam String password) {

        Trainee trainee = traineeService.findByUsername(username, password);
        return ResponseEntity.ok(TraineeMapper.toProfileResponse(trainee));
    }

    @PutMapping("/{username}")
    @ApiOperation("Update trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee updated successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<TraineeProfileResponse> update(
            @ApiParam("Trainee username") @PathVariable String username,
            @ApiParam("Update trainee request") @Valid @RequestBody UpdateTraineeRequest request) {

        if (!username.equals(request.getUsername())) {
            throw new BadRequestException("Username cannot be changed");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setActive(Boolean.TRUE.equals(request.getActive()));

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());

        Trainee updated = traineeService.update(
                request.getUsername(),
                request.getPassword(),
                trainee
        );

        return ResponseEntity.ok(TraineeMapper.toProfileResponse(updated));
    }

    @DeleteMapping("/{username}")
    @ApiOperation("Delete trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<String> delete(
            @ApiParam("Trainee username") @PathVariable String username,
            @ApiParam("User password") @RequestParam String password) {

        traineeService.deleteByUsername(username, password);
        return ResponseEntity.ok("Trainee deleted successfully");
    }

    @PatchMapping("/{username}/active")
    @ApiOperation("Activate or deactivate trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee status updated"),
            @ApiResponse(code = 400, message = "Validation or state error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<String> updateActiveStatus(
            @ApiParam("Trainee username") @PathVariable String username,
            @ApiParam("Update active status request") @Valid @RequestBody UpdateActiveStatusRequest request) {

        if (!username.equals(request.getUsername())) {
            throw new BadRequestException("Username cannot be changed");
        }

        if (Boolean.TRUE.equals(request.getActive())) {
            traineeService.activate(request.getUsername(), request.getPassword());
            return ResponseEntity.ok("Trainee activated successfully");
        } else {
            traineeService.deactivate(request.getUsername(), request.getPassword());
            return ResponseEntity.ok("Trainee deactivated successfully");
        }
    }

    @GetMapping("/{username}/unassigned-trainers")
    @ApiOperation("Get active trainers not assigned to the trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Unassigned trainers returned"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    public ResponseEntity<List<TrainerSummaryResponse>> getUnassignedTrainers(
            @ApiParam("Trainee username") @PathVariable String username,
            @ApiParam("User password") @RequestParam String password) {

        List<Trainer> trainers = traineeService.getUnassignedTrainers(username, password);

        List<TrainerSummaryResponse> response = trainers.stream()
                .map(TraineeMapper::toTrainerSummaryResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/trainers")
    @ApiOperation("Update trainee trainer list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainer list updated successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee or trainer not found")
    })
    public ResponseEntity<List<TrainerSummaryResponse>> updateTrainerList(
            @ApiParam("Trainee username") @PathVariable String username,
            @ApiParam("Update trainee trainer list request") @Valid @RequestBody UpdateTraineeTrainerListRequest request) {

        if (!username.equals(request.getTraineeUsername())) {
            throw new BadRequestException("Username cannot be changed");
        }

        List<Trainer> trainers = traineeService.updateTrainerList(
                request.getTraineeUsername(),
                request.getPassword(),
                request.getTrainerUsernames()
        );

        List<TrainerSummaryResponse> response = trainers.stream()
                .map(TraineeMapper::toTrainerSummaryResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation("Get trainee trainings by filters")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Trainee trainings returned"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainings(
            @ApiParam("Trainee username") @PathVariable String username,
            @Valid @ModelAttribute TraineeTrainingsRequest request) {

        if (!username.equals(request.getUsername())) {
            throw new BadRequestException("Username cannot be changed");
        }

        TraineeTrainingFilter filter = new TraineeTrainingFilter();
        filter.setPeriodFrom(request.getPeriodFrom());
        filter.setPeriodTo(request.getPeriodTo());
        filter.setTrainerName(request.getTrainerName());
        filter.setTrainingTypeName(request.getTrainingTypeName());

        List<TraineeTrainingResponse> response = trainingService
                .getTraineeTrainings(request.getUsername(), request.getPassword(), filter)
                .stream()
                .map(TrainingMapper::toTraineeTrainingResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}