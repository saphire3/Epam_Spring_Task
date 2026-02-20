package com.epam.training.storage;

import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.Training;

import java.util.Map;

public class InitData {

    private Map<Long, Trainee> trainees;
    private Map<Long, Trainer> trainers;
    private Map<Long, Training> trainings;

    public Map<Long, Trainee> getTrainees() {
        return trainees;
    }

    public void setTrainees(Map<Long, Trainee> trainees) {
        this.trainees = trainees;
    }

    public Map<Long, Trainer> getTrainers() {
        return trainers;
    }

    public void setTrainers(Map<Long, Trainer> trainers) {
        this.trainers = trainers;
    }

    public Map<Long, Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(Map<Long, Training> trainings) {
        this.trainings = trainings;
    }
}
