package com.epam.training.service;

import com.epam.training.exception.TrainingTypeNotFoundException;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.repository.TrainerRepository;
import com.epam.training.repository.TrainingTypeRepository;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;

    public TrainerService(TrainerRepository trainerRepository,
                          TrainingTypeRepository trainingTypeRepository,
                          UsernameGenerator usernameGenerator,
                          PasswordGenerator passwordGenerator,
                          PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new trainer. Returns array [username, plainPassword].
     */
    public String[] create(Trainer trainer, String specializationName) {
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(specializationName)
                .orElseThrow(() -> new TrainingTypeNotFoundException(specializationName));

        String username = usernameGenerator.generate(trainer.getFirstName(), trainer.getLastName());
        String rawPassword = passwordGenerator.generate();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        trainer.setUsername(username);
        trainer.setPassword(encodedPassword);
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);

        trainerRepository.save(trainer);
        log.info("Created trainer with username: {}", username);
        return new String[]{username, rawPassword};
    }

    @Transactional(readOnly = true)
    public Trainer findByUsername(String username) {
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found: {}", username);
                    return new UserNotFoundException("Trainer not found: " + username);
                });
    }

    public Trainer update(String username, Trainer updated, String specializationName) {
        Trainer existing = findByUsername(username);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        if (specializationName != null) {
            TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(specializationName)
                    .orElseThrow(() -> new TrainingTypeNotFoundException(specializationName));
            existing.setSpecialization(trainingType);
        }
        Trainer saved = trainerRepository.save(existing);
        log.info("Updated trainer: {}", username);
        return saved;
    }

    public void activate(String username) {
        Trainer trainer = findByUsername(username);
        trainer.setActive(true);
        trainerRepository.save(trainer);
        log.info("Activated trainer: {}", username);
    }

    public void deactivate(String username) {
        Trainer trainer = findByUsername(username);
        trainer.setActive(false);
        trainerRepository.save(trainer);
        log.info("Deactivated trainer: {}", username);
    }

    public void changePassword(String username, String newRawPassword) {
        Trainer trainer = findByUsername(username);
        trainer.setPassword(passwordEncoder.encode(newRawPassword));
        trainerRepository.save(trainer);
        log.info("Password changed for trainer: {}", username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> findAll() {
        return trainerRepository.findAll();
    }
}
