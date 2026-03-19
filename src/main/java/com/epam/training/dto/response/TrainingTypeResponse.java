package com.epam.training.dto.response;

public class TrainingTypeResponse {
    private Long id;
    private String trainingTypeName;

    public TrainingTypeResponse() {
    }

    public TrainingTypeResponse(Long id, String trainingTypeName) {
        this.id = id;
        this.trainingTypeName = trainingTypeName;
    }

    public Long getId() {
        return id;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}