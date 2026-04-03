package com.epam.training.dto.response;

import java.util.List;

public class TrainerProfileResponse {

    private String firstName;
    private String lastName;
    private String specializationName;
    private boolean isActive;
    private List<TraineeSummaryResponse> trainees;

    public TrainerProfileResponse() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<TraineeSummaryResponse> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<TraineeSummaryResponse> trainees) {
        this.trainees = trainees;
    }
}
