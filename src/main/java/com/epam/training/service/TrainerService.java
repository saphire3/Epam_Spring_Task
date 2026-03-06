package com.epam.training.service;

import com.epam.training.dao.TrainerDao;
import com.epam.training.dao.TrainingTypeDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final AuthService authService;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TrainerService(TrainerDao trainerDao,
                          TrainingTypeDao trainingTypeDao,
                          AuthService authService,
                          UsernameGenerator usernameGenerator,
                          PasswordGenerator passwordGenerator) {
        this.trainerDao = trainerDao;
        this.trainingTypeDao = trainingTypeDao;
        this.authService = authService;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    public Trainer create(Trainer trainer, String specializationName) {
        validateTrainerForCreateOrUpdate(trainer);

        if (StringUtils.isBlank(specializationName)) {
            throw new IllegalArgumentException("Specialization name cannot be blank");
        }

        TrainingType specialization = trainingTypeDao.findByName(specializationName)
                .orElseThrow(() -> new IllegalArgumentException("Unknown specialization: " + specializationName));

        User user = trainer.getUser();
        String username = usernameGenerator.generate(user.getFirstName(), user.getLastName());
        user.setUsername(username);
        user.setPassword(passwordGenerator.generate());
        user.setActive(true);
        trainer.setSpecialization(specialization);

        trainerDao.save(trainer);
        log.info("Created trainer profile for username={}", username);
        return trainer;
    }

    @Transactional(readOnly = true)
    public Trainer findByUsername(String username, String password) {
        validateUsername(username);
        authService.requireTrainerAuth(username, password);

        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + username));
    }

    public Trainer update(String username, String password, Trainer updated, String specializationName) {
        validateUsername(username);
        authService.requireTrainerAuth(username, password);
        validateTrainerForCreateOrUpdate(updated);

        Trainer existing = trainerDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + username));

        existing.getUser().setFirstName(updated.getUser().getFirstName());
        existing.getUser().setLastName(updated.getUser().getLastName());

        if (StringUtils.isNotBlank(specializationName)) {
            TrainingType specialization = trainingTypeDao.findByName(specializationName)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown specialization: " + specializationName));
            existing.setSpecialization(specialization);
        }

        Trainer saved = trainerDao.update(existing);
        log.info("Updated trainer profile for username={}", username);
        return saved;
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        validateUsername(username);
        authService.requireTrainerAuth(username, oldPassword);

        if (StringUtils.isBlank(newPassword)) {
            throw new IllegalArgumentException("New password cannot be blank");
        }
        if (newPassword.equals(oldPassword)) {
            throw new IllegalArgumentException("New password must be different from old password");
        }

        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + username));

        trainer.getUser().setPassword(newPassword);
        trainerDao.update(trainer);
        log.info("Changed password for trainer username={}", username);
    }

    public void activate(String username, String password) {
        validateUsername(username);
        authService.requireTrainerAuth(username, password);

        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + username));

        if (trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer is already active");
        }

        trainer.getUser().setActive(true);
        trainerDao.update(trainer);
        log.info("Activated trainer username={}", username);
    }

    public void deactivate(String username, String password) {
        validateUsername(username);
        authService.requireTrainerAuth(username, password);

        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + username));

        if (!trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer is already inactive");
        }

        trainer.getUser().setActive(false);
        trainerDao.update(trainer);
        log.info("Deactivated trainer username={}", username);
    }

    @Transactional(readOnly = true)
    public Trainer getByUsername(String username, String password) {
        return findByUsername(username, password);
    }

    @Transactional(readOnly = true)
    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }

    private void validateTrainerForCreateOrUpdate(Trainer trainer) {
        if (trainer == null || trainer.getUser() == null) {
            throw new IllegalArgumentException("Trainer and trainer user must not be null");
        }

        if (StringUtils.isBlank(trainer.getUser().getFirstName())) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        if (StringUtils.isBlank(trainer.getUser().getLastName())) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
    }

    private void validateUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
    }
}