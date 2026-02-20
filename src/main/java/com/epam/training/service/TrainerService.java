package com.epam.training.service;

import com.epam.training.dao.TrainerDao;
import com.epam.training.model.Trainer;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import com.epam.training.exception.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);
    private static final AtomicLong idGenerator = new AtomicLong(1);

    private TrainerDao trainerDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public Trainer create(Trainer trainer) {

        validate(trainer);

        log.info("Creating trainer {} {}", trainer.getFirstName(), trainer.getLastName());

        Set<String> existingUsernames = trainerDao.findAll()
                .stream()
                .map(Trainer::getUsername)
                .collect(Collectors.toSet());

        String username = usernameGenerator.generate(
                trainer.getFirstName(),
                trainer.getLastName(),
                existingUsernames
        );

        trainer.setUsername(username);
        trainer.setPassword(passwordGenerator.generate());
        trainer.setId(idGenerator.getAndIncrement());

        trainerDao.save(trainer.getId(), trainer);

        log.debug("Trainer created with username {}", username);

        return trainer;
    }

    public Trainer find(Long id) {
        log.debug("Finding trainer by id {}", id);
        return trainerDao.findById(id);
    }

    public Trainer update(Long id, Trainer updated) {

        validate(updated);

        log.info("Updating trainer with id {}", id);

        Trainer existing = trainerDao.findById(id);

        if (existing == null) {
            log.error("Trainer not found with id {}", id);
            throw new EntityNotFoundException("Trainer not found with id " + id);
        }

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setSpecialization(updated.getSpecialization());

        trainerDao.save(id, existing);

        return existing;
    }

    private void validate(Trainer trainer) {
        if (trainer.getFirstName() == null || trainer.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }

        if (trainer.getLastName() == null || trainer.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
    }

    public java.util.Collection<Trainer> findAll() {
        return trainerDao.findAll();
    }
}