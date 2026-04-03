package com.epam.training.service;

import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.repository.TraineeRepository;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;

    public TraineeService(TraineeRepository traineeRepository,
                          TrainerRepository trainerRepository,
                          UsernameGenerator usernameGenerator,
                          PasswordGenerator passwordGenerator,
                          PasswordEncoder passwordEncoder) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new trainee. Returns array [username, plainPassword].
     * The plain password is only returned this once; the stored password is BCrypt-encoded.
     */
    public String[] create(Trainee trainee) {
        String username = usernameGenerator.generate(trainee.getFirstName(), trainee.getLastName());
        String rawPassword = passwordGenerator.generate();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        trainee.setUsername(username);
        trainee.setPassword(encodedPassword);
        trainee.setActive(true);

        traineeRepository.save(trainee);
        log.info("Created trainee with username: {}", username);
        return new String[]{username, rawPassword};
    }

    @Transactional(readOnly = true)
    public Trainee findByUsername(String username) {
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", username);
                    return new UserNotFoundException("Trainee not found: " + username);
                });
    }

    public Trainee update(String username, Trainee updated) {
        Trainee existing = findByUsername(username);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        if (updated.getDateOfBirth() != null) {
            existing.setDateOfBirth(updated.getDateOfBirth());
        }
        if (updated.getAddress() != null) {
            existing.setAddress(updated.getAddress());
        }
        Trainee saved = traineeRepository.save(existing);
        log.info("Updated trainee: {}", username);
        return saved;
    }

    public void delete(String username) {
        Trainee trainee = findByUsername(username);
        traineeRepository.delete(trainee);
        log.info("Deleted trainee: {}", username);
    }

    public void activate(String username) {
        Trainee trainee = findByUsername(username);
        trainee.setActive(true);
        traineeRepository.save(trainee);
        log.info("Activated trainee: {}", username);
    }

    public void deactivate(String username) {
        Trainee trainee = findByUsername(username);
        trainee.setActive(false);
        traineeRepository.save(trainee);
        log.info("Deactivated trainee: {}", username);
    }

    public void changePassword(String username, String newRawPassword) {
        Trainee trainee = findByUsername(username);
        trainee.setPassword(passwordEncoder.encode(newRawPassword));
        traineeRepository.save(trainee);
        log.info("Password changed for trainee: {}", username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        return traineeRepository.findTrainersNotAssignedToTrainee(traineeUsername);
    }

    public List<Trainer> updateTrainerList(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = findByUsername(traineeUsername);
        List<Trainer> trainers = trainerRepository.findByUsernameIn(trainerUsernames);
        trainee.setTrainers(new HashSet<>(trainers));
        traineeRepository.save(trainee);
        log.info("Updated trainer list for trainee: {}", traineeUsername);
        return trainers;
    }
}
