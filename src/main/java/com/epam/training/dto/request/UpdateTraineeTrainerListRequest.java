package com.epam.training.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UpdateTraineeTrainerListRequest {

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Trainer usernames list is required")
    private List<String> trainerUsernames;

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getTrainerUsernames() {
        return trainerUsernames;
    }

    public void setTrainerUsernames(List<String> trainerUsernames) {
        this.trainerUsernames = trainerUsernames;
    }
}