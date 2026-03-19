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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public TraineeController(TraineeService traineeService,
                             TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody TraineeRegistrationRequest request) {
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
    public ResponseEntity<TraineeProfileResponse> getProfile(@PathVariable String username,
                                                             @RequestParam String password) {
        Trainee trainee = traineeService.findByUsername(username, password);
        return ResponseEntity.ok(TraineeMapper.toProfileResponse(trainee));
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> update(@PathVariable String username,
                                                         @Valid @RequestBody UpdateTraineeRequest request) {
        if (!username.equals(request.getUsername())) {
            throw new BadRequestException("Username cannot be changed");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

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
    public ResponseEntity<String> delete(@PathVariable String username,
                                         @RequestParam String password) {
        traineeService.deleteByUsername(username, password);
        return ResponseEntity.ok("Trainee deleted successfully");
    }

    @PatchMapping("/{username}/active")
    public ResponseEntity<String> updateActiveStatus(@PathVariable String username,
                                                     @Valid @RequestBody UpdateActiveStatusRequest request) {
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
    public ResponseEntity<List<TrainerSummaryResponse>> getUnassignedTrainers(@PathVariable String username,
                                                                              @RequestParam String password) {
        List<Trainer> trainers = traineeService.getUnassignedTrainers(username, password);

        List<TrainerSummaryResponse> response = trainers.stream()
                .map(TraineeMapper::toTrainerSummaryResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> updateTrainerList(@PathVariable String username,
                                                                          @Valid @RequestBody UpdateTraineeTrainerListRequest request) {
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
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainings(@PathVariable String username,
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