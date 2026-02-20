package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.model.Trainee;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import com.epam.training.exception.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);
    private static final AtomicLong idGenerator = new AtomicLong(1);

    private TraineeDao traineeDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public Trainee create(Trainee trainee) {

        validate(trainee);

        log.info("Creating trainee {} {}", trainee.getFirstName(), trainee.getLastName());

        Set<String> existingUsernames = traineeDao.findAll()
                .stream()
                .map(Trainee::getUsername)
                .collect(Collectors.toSet());

        String username = usernameGenerator.generate(
                trainee.getFirstName(),
                trainee.getLastName(),
                existingUsernames
        );

        trainee.setUsername(username);
        trainee.setPassword(passwordGenerator.generate());
        trainee.setId(idGenerator.getAndIncrement());

        traineeDao.save(trainee.getId(), trainee);

        log.debug("Trainee created with username {}", username);

        return trainee;
    }

    public Trainee find(Long id) {
        log.debug("Finding trainee by id {}", id);
        return traineeDao.findById(id);
    }

    public void delete(Long id) {
        log.info("Deleting trainee with id {}", id);
        traineeDao.delete(id);
    }

    public Trainee update(Long id, Trainee updated) {

        validate(updated);

        log.info("Updating trainee with id {}", id);

        Trainee existing = traineeDao.findById(id);

        if (existing == null) {
            log.error("Trainee not found with id {}", id);
            throw new EntityNotFoundException("Trainee not found with id " + id);
        }

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setAddress(updated.getAddress());
        existing.setDateOfBirth(updated.getDateOfBirth());

        traineeDao.save(id, existing);

        return existing;
    }

    private void validate(Trainee trainee) {

        if (trainee.getFirstName() == null || trainee.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }

        if (trainee.getLastName() == null || trainee.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
    }

    public java.util.Collection<Trainee> findAll() {
        return traineeDao.findAll();
    }
}