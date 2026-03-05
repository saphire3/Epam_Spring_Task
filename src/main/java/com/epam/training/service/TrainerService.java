package com.epam.training.service;

import com.epam.training.dao.TrainerDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.model.Trainer;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerService {

    private final TrainerDao trainerDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TrainerService(TrainerDao trainerDao,
                          UsernameGenerator usernameGenerator,
                          PasswordGenerator passwordGenerator) {
        this.trainerDao = trainerDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    public Trainer create(Trainer trainer) {

        validate(trainer);

        String username = usernameGenerator.generate(
                trainer.getFirstName(),
                trainer.getLastName()
        );

        trainer.setUsername(username);
        trainer.setPassword(passwordGenerator.generate());

        Long id = (long) (trainerDao.findAll().size() + 1);
        trainer.setId(id);

        trainerDao.save(id, trainer);

        return trainer;
    }

    public Trainer update(Long id, Trainer updated) {

        Trainer existing = trainerDao.getById(id);

        if (existing == null) {
            throw new UserNotFoundException(id);
        }

        validate(updated);

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setSpecialization(updated.getSpecialization());

        trainerDao.save(id, existing);

        return existing;
    }

    public Trainer getTrainerById(Long id) {

        Trainer trainer = trainerDao.getById(id);

        if (trainer == null) {
            throw new UserNotFoundException(id);
        }

        return trainer;
    }
    public void delete(Long id) {

        Trainer trainee = trainerDao.getById(id);

        if (trainee == null) {
            throw new UserNotFoundException(id);
        }

        trainerDao.delete(id);
    }

    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }

    private void validate(Trainer trainer) {

        if (StringUtils.isBlank(trainer.getFirstName())) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }

        if (StringUtils.isBlank(trainer.getLastName())) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
    }
}