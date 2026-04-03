package com.epam.training.dto.response;

public class TrainerSummaryResponse {

    private String username;
    private String firstName;
    private String lastName;
    private String specializationName;

    public TrainerSummaryResponse() {
    }

    public TrainerSummaryResponse(String username, String firstName, String lastName, String specializationName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specializationName = specializationName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
