package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.exception.UserNotFoundException;
import com.epam.training.model.Trainee;
import com.epam.training.util.PasswordGenerator;
import com.epam.training.util.UsernameGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {

    private final TraineeDao traineeDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TraineeService(TraineeDao traineeDao,
                          UsernameGenerator usernameGenerator,
                          PasswordGenerator passwordGenerator) {
        this.traineeDao = traineeDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    public Trainee create(Trainee trainee) {

        validate(trainee);

        String username = usernameGenerator.generate(
                trainee.getFirstName(),
                trainee.getLastName()
        );

        trainee.setUsername(username);
        trainee.setPassword(passwordGenerator.generate());

        Long id = (long) (traineeDao.findAll().size() + 1);
        trainee.setId(id);

        traineeDao.save(id, trainee);

        return trainee;
    }

    public Trainee update(Long id, Trainee updated) {

        Trainee existing = traineeDao.getById(id);

        if (existing == null) {
            throw new UserNotFoundException(id);
        }

        validate(updated);

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setAddress(updated.getAddress());
        existing.setDateOfBirth(updated.getDateOfBirth());

        traineeDao.save(id, existing);

        return existing;
    }

    public Trainee getTraineeById(Long id) {

        Trainee trainee = traineeDao.getById(id);

        if (trainee == null) {
            throw new UserNotFoundException(id);
        }

        return trainee;
    }

    public void delete(Long id) {

        Trainee trainee = traineeDao.getById(id);

        if (trainee == null) {
            throw new UserNotFoundException(id);
        }

        traineeDao.delete(id);
    }

    private void validate(Trainee trainee) {
        if (StringUtils.isBlank(trainee.getFirstName())) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }

        if (StringUtils.isBlank(trainee.getLastName())) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
    }
}