package com.epam.training.controller;

import com.epam.training.dto.request.ChangePasswordRequest;
import com.epam.training.dto.request.LoginRequest;
import com.epam.training.exception.AuthenticationException;
import com.epam.training.service.AuthService;
import com.epam.training.service.TraineeService;
import com.epam.training.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "Authentication")
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
    @ApiOperation("Authenticate user by username and password")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Login successful"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 400, message = "Validation error")
    })
    public ResponseEntity<String> login(
            @Valid @ModelAttribute LoginRequest request) {
        authService.requireAnyUserAuth(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("Login successful");
    }

    @PutMapping("/change-password")
    @ApiOperation("Change password for authenticated trainee or trainer")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password changed successfully"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 400, message = "Validation error")
    })
    public ResponseEntity<String> changePassword(
            @ApiParam("Change password request")
            @Valid @RequestBody ChangePasswordRequest request) {
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