package com.epam.training.controller;

import com.epam.training.dto.request.ChangePasswordRequest;
import com.epam.training.dto.request.LoginRequest;
import com.epam.training.exception.AuthenticationException;
import com.epam.training.service.AuthService;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public AuthController(AuthService authService,
                          TraineeService traineeService,
                          TrainerService trainerService) {
        this.authService = authService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@Valid @ModelAttribute LoginRequest request) {
        authService.requireAnyUserAuth(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("Login successful");
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        if (authService.authenticateTrainee(request.getUsername(), request.getOldPassword())) {
            traineeService.changePassword(
                    request.getUsername(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok("Password changed successfully");
        }

        if (authService.authenticateTrainer(request.getUsername(), request.getOldPassword())) {
            trainerService.changePassword(
                    request.getUsername(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok("Password changed successfully");
        }

        throw new AuthenticationException("Invalid username or password");
    }
}