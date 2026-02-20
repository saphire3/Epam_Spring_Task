package com.epam.training.util;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UsernameGenerator {

    public String generate(String firstName, String lastName, Set<String> existing) {

        String base = firstName + "." + lastName;
        String username = base;
        int counter = 1;

        while (existing.contains(username)) {
            username = base + counter;
            counter++;
        }

        return username;
    }
}
