package com.epam.training.exception;

public class UserBlockedException extends RuntimeException {

    public UserBlockedException(String username) {
        super("User is temporarily blocked due to too many failed login attempts: " + username);
    }
}
