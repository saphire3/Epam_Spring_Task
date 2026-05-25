package com.epam.workload.dto;

import java.util.Map;

public class TrainerSummaryResponse {

    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean trainerStatus;
    private Map<Integer, Map<Integer, Integer>> years;

    public TrainerSummaryResponse() {}

    public TrainerSummaryResponse(String trainerUsername, String trainerFirstName, String trainerLastName,
                                   boolean trainerStatus, Map<Integer, Map<Integer, Integer>> years) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.trainerStatus = trainerStatus;
        this.years = years;
    }

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getTrainerFirstName() { return trainerFirstName; }
    public void setTrainerFirstName(String trainerFirstName) { this.trainerFirstName = trainerFirstName; }

    public String getTrainerLastName() { return trainerLastName; }
    public void setTrainerLastName(String trainerLastName) { this.trainerLastName = trainerLastName; }

    public boolean isTrainerStatus() { return trainerStatus; }
    public void setTrainerStatus(boolean trainerStatus) { this.trainerStatus = trainerStatus; }

    public Map<Integer, Map<Integer, Integer>> getYears() { return years; }
    public void setYears(Map<Integer, Map<Integer, Integer>> years) { this.years = years; }
}