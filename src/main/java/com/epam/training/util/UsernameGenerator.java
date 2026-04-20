package com.epam.training.util;

import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.repository.TraineeRepository;
import com.epam.training.repository.TrainerRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UsernameGenerator {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public UsernameGenerator(TraineeRepository traineeRepository,
                             TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    public String generate(String firstName, String lastName) {
        Set<String> existing = Stream.concat(
                traineeRepository.findAll().stream().map(Trainee::getUsername),
                trainerRepository.findAll().stream().map(Trainer::getUsername)
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        String base = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = base;
        int counter = 1;
        while (existing.contains(username)) {
            username = base + counter++;
        }
        return username;
    }
}