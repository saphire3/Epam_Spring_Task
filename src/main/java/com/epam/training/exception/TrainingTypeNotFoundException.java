package com.epam.training.exception;

public class TrainingTypeNotFoundException extends RuntimeException {

    public TrainingTypeNotFoundException(String name) {
        super("TrainingType not found: " + name);
    }
}
