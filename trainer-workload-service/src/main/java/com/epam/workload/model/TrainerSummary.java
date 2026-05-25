package com.epam.workload.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrainerSummary {

    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    // year -> (month -> total duration in minutes)
    private Map<Integer, Map<Integer, Integer>> years = new ConcurrentHashMap<>();

    public TrainerSummary() {}

    public TrainerSummary(String username, String firstName, String lastName, boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Map<Integer, Map<Integer, Integer>> getYears() { return years; }
    public void setYears(Map<Integer, Map<Integer, Integer>> years) { this.years = years; }
}