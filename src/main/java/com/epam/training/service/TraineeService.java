package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dao.TrainerDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.model.User;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final AuthService authService;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TraineeService(TraineeDao traineeDao,
                          TrainerDao trainerDao,
                          AuthService authService,
                          UsernameGenerator usernameGenerator,
                          PasswordGenerator passwordGenerator) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.authService = authService;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    public Trainee create(Trainee trainee) {
        validateTraineeForCreateOrUpdate(trainee);

        User user = trainee.getUser();
        String username = usernameGenerator.generate(user.getFirstName(), user.getLastName());
        user.setUsername(username);
        user.setPassword(passwordGenerator.generate());
        user.setActive(true);

        traineeDao.save(trainee);
        log.info("Created trainee profile for username={}", username);
        return trainee;
    }

    @Transactional(readOnly = true)
    public Trainee findByUsername(String username, String password) {
        validateUsername(username);
        authService.requireTraineeAuth(username, password);

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + username));
    }

    public Trainee update(String username, String password, Trainee updated) {
        validateUsername(username);
        authService.requireTraineeAuth(username, password);
        validateTraineeForCreateOrUpdate(updated);

        Trainee existing = traineeDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + username));

        existing.getUser().setFirstName(updated.getUser().getFirstName());
        existing.getUser().setLastName(updated.getUser().getLastName());
        existing.setAddress(updated.getAddress());
        existing.setDateOfBirth(updated.getDateOfBirth());

        Trainee saved = traineeDao.update(existing);
        log.info("Updated trainee profile for username={}", username);
        return saved;
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        validateUsername(username);
        authService.requireTraineeAuth(username, oldPassword);

        if (StringUtils.isBlank(newPassword)) {
            throw new IllegalArgumentException("New password cannot be blank");
        }
        if (newPassword.equals(oldPassword)) {
            throw new IllegalArgumentException("New password must be different from old password");
        }

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + username));

        trainee.getUser().setPassword(newPassword);
        traineeDao.update(trainee);
        log.info("Changed password for trainee username={}", username);
    }

    public void activate(String username, String password) {
        validateUsername(username);
        authService.requireTraineeAuth(username, password);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + username));

        if (trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee is already active");
        }

        trainee.getUser().setActive(true);
        traineeDao.update(trainee);
        log.info("Activated trainee username={}", username);
    }

    public void deactivate(String username, String password) {
        validateUsername(username);
        authService.requireTraineeAuth(username, password);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + username));

        if (!trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee is already inactive");
        }

        trainee.getUser().setActive(false);
        traineeDao.update(trainee);
        log.info("Deactivated trainee username={}", username);
    }

    public void deleteByUsername(String username, String password) {
        validateUsername(username);
        authService.requireTraineeAuth(username, password);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + username));

        traineeDao.delete(trainee);
        log.info("Deleted trainee username={}", username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String traineeUsername, String password) {
        validateUsername(traineeUsername);
        authService.requireTraineeAuth(traineeUsername, password);

        traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + traineeUsername));

        log.info("Requested unassigned trainers for trainee username={}", traineeUsername);
        return trainerDao.findNotAssignedToTrainee(traineeUsername);
    }

    public void updateTrainerList(String traineeUsername, String password, List<String> trainerUsernames) {
        validateUsername(traineeUsername);
        authService.requireTraineeAuth(traineeUsername, password);

        if (trainerUsernames == null) {
            throw new IllegalArgumentException("Trainer usernames list cannot be null");
        }

        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + traineeUsername));

        Set<Trainer> newTrainers = new HashSet<>();
        for (String trainerUsername : trainerUsernames) {
            validateUsername(trainerUsername);

            Trainer trainer = trainerDao.findByUsername(trainerUsername)
                    .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + trainerUsername));

            newTrainers.add(trainer);
        }

        trainee.setTrainers(newTrainers);
        traineeDao.update(trainee);
        log.info("Updated trainer list for trainee username={}", traineeUsername);
    }

    private void validateTraineeForCreateOrUpdate(Trainee trainee) {
        if (trainee == null || trainee.getUser() == null) {
            throw new IllegalArgumentException("Trainee and trainee user must not be null");
        }

        if (StringUtils.isBlank(trainee.getUser().getFirstName())) {
            throw new IllegalArgumentException("First name cannot be blank");
        }

        if (StringUtils.isBlank(trainee.getUser().getLastName())) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
    }

    private void validateUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
    }
}